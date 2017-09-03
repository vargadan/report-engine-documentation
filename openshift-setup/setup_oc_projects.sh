#!/bin/bash

oc new-project reportengine-dev --display-name="Report Engine - DEV"
oc new-project reportengine-it --display-name="Report Engine - IT"
oc new-project reportengine-cicd --display-name="Report Engine - CICD"

oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:default -n reportengine-dev
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:default -n reportengine-cicd
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:jenkins -n reportengine-dev
oc policy add-role-to-user edit system:serviceaccount:reportengine-cicd:jenkins -n reportengine-cicd
oc process -f jenkins.yaml | oc create -f -
oc process -f nexus.yaml | oc create -f -
oc process -f sonarqube.yaml | oc create -f -
