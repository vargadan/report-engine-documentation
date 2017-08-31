#!/bin/bash
oc login -u system:admin

oc delete project reportengine-cicd
oc delete project reportengine-dev
oc delete project reportengine-it

oc new-project reportengine-dev --display-name="Report Engine - DEV"
oc new-project reportengine-it --display-name="Report Engine - IT"
oc new-project reportengine-cicd --display-name="Report Engine - CICD"

oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:default -n reportengine-dev
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:default -n reportengine-infra
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:jenkins -n reportengine-dev
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:jenkins -n reportengine-infra
oc process -f jenkins.yaml | oc create -f -

oc logout

oc login -u developer
