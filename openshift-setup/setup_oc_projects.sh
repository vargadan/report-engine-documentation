#!/bin/bash

oc new-project ctr-cicd --display-name="Client Tax Reporting - CICD"
oc policy add-role-to-user edit system:serviceaccount:ctr-cicd:jenkins -n ctr-cicd

oc process -f nexus.yaml | oc create -f - -n ctr-cicd
oc process -f pipelines.yaml | oc create -f - -n ctr-cicd

oc new-project ctr-dev --display-name="Client Tax Reporting - DEV"
oc policy add-role-to-user edit system:serviceaccount:ctr-cicd:jenkins -n ctr-dev
oc process -f rabbitmq.yaml | oc create -f - -n ctr-dev

oc new-project ctr-it --display-name="Client Tax Reporting - IT"
oc policy add-role-to-user edit system:serviceaccount:ctr-cicd:jenkins -n ctr-it
oc process -f rabbitmq.yaml | oc create -f - -n ctr-it

oc new-project ctr-prod --display-name="Client Tax Reporting - PROD"
oc policy add-role-to-user edit system:serviceaccount:ctr-cicd:jenkins -n ctr-prod
oc process -f rabbitmq.yaml | oc create -f - -n ctr-prod

oc project ctr-cicd