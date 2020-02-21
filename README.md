# gitlab.cla project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `git-eca-rest-api-0.0.1-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/git-eca-rest-api-0.0.1-runner.jar`.

## Enabling commit hook in GitLab

To enable the Git hook that makes use of this service, a running GitLab instance will be needed with shell access. This instruction set assumes that the running GitLab instance runs using the Omnibus set up rather than the source. For the differences in process, please see the [GitLab custom hook administration instructions](https://docs.gitlab.com/ee/administration/custom_hooks.html). Once obtained, the following steps can be used to start or update the hook.

1. Access the GitLab server shell, and create a folder at `/opt/gitlab/embedded/service/gitlab-shell/hooks/pre-receive.d/` if it doesn't already exist. This folder will contain all of the servers global Git hooks for pre-receive events. These hooks trigger when a user attempts to push information to the server.  
1. In the host machine, copy the ECA script to the newly created folder. If using a docker container, this can be done with a call similar to the following:  
`docker cp src/main/rb/eca.rb gitlab.eca_web_1:/opt/gitlab/embedded/service/gitlab-shell/hooks/pre-receive.d/`

1. In the GitLab shell once again, ensure that the newly copied script matches the folders ownership, and that the file permissions are `755`. This allows the server to properly run the hook when needed.