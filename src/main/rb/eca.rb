#!/usr/bin/env ruby
# encoding: UTF-8

require 'json'
require 'httparty'
require 'multi_json'

## Process the commit into a hash object that will be posted to the ECA validation service
def process_commit(sha)
  commit_parents_raw = `git show -s --format='%P' #{sha}`
  commit_parents = commit_parents_raw.split(/\s/)
  return {
    :author => {
      :name => `git show -s --format='%an' #{sha}`.force_encoding("utf-8"),
      :mail => `git show -s --format='%ae' #{sha}`.force_encoding("utf-8"),
    },
    :committer => {
      :name => `git show -s --format='%cn' #{sha}`.force_encoding("utf-8"),
      :mail => `git show -s --format='%ce' #{sha}`.force_encoding("utf-8"),
    },
    :body => `git show -s --format='%B' #{sha}`.force_encoding("utf-8"),
    :subject => `git show -s --format='%s' #{sha}`.force_encoding("utf-8"),
    :hash => `git show -s --format='%H' #{sha}`,
    :parents => commit_parents
  }
end

## read in the access token from secret file
if (!File.file?("/etc/gitlab/eca-access-token"))
  puts "GL-HOOK-ERR: Internal server error, please contact administrator. Error, secret not found" 
  exit 1
end
access_token_file = File.open("/etc/gitlab/eca-access-token")
access_token = access_token_file.read.chomp
access_token_file.close
if (access_token.empty?)
  puts "GL-HOOK-ERR: Internal server error, please contact administrator. Error, secret not found" 
  exit 1
end

## Read in the arguments passed from GitLab and split them to an arg array
stdin_raw = ARGF.read;
stdin_args = stdin_raw.split(/\s+/)

## Set the vars for the commit hashes of current pre-receive event
previous_head_commit = stdin_args[0]
new_head_commit = stdin_args[1]

## Get the project ID from env var, extracting from pattern 'project-###'
project_id = ENV['GL_REPOSITORY'][8..-1]
## Get data about project from API
project_response = HTTParty.get("https://gitlab.eclipse.org/api/v4/projects/#{project_id}", 
  :headers => {
    'Authorization' => 'Bearer ' + access_token
  })
## Format data to be able to easily read and process it
project_json_data = MultiJson.load(project_response.body)
## Get the web URL, checking if project is a fork to get original project URL
if (!project_json_data['forked_from_project'].nil? && !project_json_data['forked_from_project']['web_url'].nil?)
  project_url = project_json_data['forked_from_project']['web_url']
else 
  project_url = project_json_data['web_url']
end

## required for proper cherry-picking of new commits
default_branch = project_json_data['default_branch']
default_branch_head_ref = "refs/heads/#{default_branch}"

## Get all commits visible relative to default branch (anything new and not merged in)
## If merging into a non-default branch this will check more than necessary, but not the whole history (which it was previously)
diff_git_commits_raw = `git cherry #{default_branch_head_ref} #{new_head_commit}`
diff_git_commits = diff_git_commits_raw.split(/\n/)

counter = 0
git_commits = []
previous_head_idx = 0
diff_git_commits.each do |commit|
  if (commit.match(/\+ ?(\S{2,})/)) then
    ## gsub will return no data for no match, as + indicates new changes and - indicates tracked changes
    cleaned_commit_sha = commit.gsub(/\+ ?(\S{2,})/, '\1')
    if (!cleaned_commit_sha.empty?) then
      git_commits.push(cleaned_commit_sha)
    end
    if (cleaned_commit_sha == previous_head_commit) then
      found_previous_head = true
      previous_head_idx = counter
    end
    counter = counter + 1
  end
end

processed_git_data = []
git_commits[previous_head_idx...].each do |commit|
  processed_git_data.push(process_commit(commit))
end

## Create the JSON payload
json_data = {
  :repoUrl => project_url,
  :provider => 'gitlab',
  :commits => processed_git_data
}
## Generate request (use gsub to scrub any lingering \n constants)
response = HTTParty.post("https://api.eclipse.org/git/eca", :body => MultiJson.dump(json_data).gsub('\\n', ''), 
  :headers => { 
    'Content-Type' => 'application/json',
    'charset' => 'utf-8'
  })
## convert request to hash map
parsed_response = Hash.new
begin
  parsed_response = MultiJson.load(response.body)
rescue MultiJson::ParseError
  puts "GL-HOOK-ERR: Unable to validate commit, server error encountered.\n\nPlease contact the administrator, and retry the commit at a later time.\n\n"
  exit 1
else
  ## for each discovered hash commit tracked by response, report if it was OK
  commit_keys = parsed_response['commits'].keys
  commit_keys.each do |key|
    commit_status = parsed_response['commits'][key]
    if (commit_status['errors'].empty?) then
      puts "Commit: #{key}\t\t✔\n\n"
      commit_status['messages'].each do |msg|
        puts "\t#{msg['message']}"
      end
      if (commit_status['warnings'].empty?) then
        commit_status['warnings'].each do |msg|
          puts "\t#{msg['message']}"
        end
        puts "Any warnings noted above may indicate compliance issues with committer ECA requirements. More information may be found on https://www.eclipse.org/legal/ECA.php"
      end
      puts "\n\n"
    else
      puts "Commit: #{key}\t\tX\n\n"
      commit_status['messages'].each do |msg|
        puts "\t#{msg['message']}"
      end
      puts "\n"
      commit_status['errors'].each do |error|
        puts "GL-HOOK-ERR: #{error['message']}"
      end
      puts "\n\n"
    end
  end
end
## If error, exit as status 1
if (response.code == 403) then
  exit 1
end
