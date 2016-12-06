#!/usr/bin/env bash

function runUITests {
    sudo rm -r $HOME/.m2/repository/org/motechproject/motech-uitestframework/0.27.1-SNAPSHOT
    mvn clean install -pl "ebodac" -Pci-remote -Dserver.url="$instance_url" -Dlogin.username="$remote_username" -Dlogin.password="$remote_password" -Dadmin.login="$admin_login"  -Dadmin.password="$admin_password" -Danalyst.login="$analyst_login" -Danalyst.password="$analyst_password"
}

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    if [ "$TEST" = "it" ]; then
        echo "USE mysql;\nUPDATE user SET password=PASSWORD('password') WHERE user='root';\nFLUSH PRIVILEGES\n" | mysql -u root
        mvn clean install -PIT -U
    elif [ "$TEST" = "ui" ]; then
        if [ "$UI_TESTS_ENABLED" = "true" ]; then
            runUITests
        else
            echo "UI Tests skipped"
        fi
    fi
elif [ "$TRAVIS_EVENT_TYPE" = "api" ] && [ "$RUN_UI_TESTS" = "true" ]; then
    runUITests
fi
