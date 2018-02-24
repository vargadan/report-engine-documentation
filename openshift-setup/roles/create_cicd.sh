#/bin/bash!
oc delete clusterrole cicd
oc create -f cicd.yaml
oc policy add-role-to-user cicd system:serviceaccount:reportengine-cicd:jenkins -n reportengine-dev
oc policy add-role-to-user cicd system:serviceaccount:reportengine-cicd:jenkins -n reportengine-it
oc get rolebindings