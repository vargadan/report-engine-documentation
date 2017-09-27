# reporting-engine
sample cloud native application composed of multiple microservices

## services
* Report Order Manager: Provides a REST API to order reports and follow the report creation process.
  * when an order is recieved in the REST API (POST to /orders) it places a message in the _orderTopic_ AMQP topic.
  * it updates the status of the report according to messages received on the _statusUpdateTopic_.
* Report Factories: Responsible for creating the report of a given type (product). A typical implementation would download data from so called data provider systems and produce and XML file representing the report. 
  * processes orders received on the _orderTopic_
  * send status update messages to the _statusUpdateTopic_
  * places the produced report as XML data onto the the _reportFileTopic_
* Report Repository
  * reads message from _reportFileTopic_ and saves them into a repository for later retrieval
* Report Renderer
  * provides an HTTP API to read reports in the requested format (as per the URI of the file request)
  * transforms XML into the requested format (currently only PDF supported)
* Report Uploader
  * reads message from _reportFileTopic_ and uploads them to an external FTP(S) system as per the configuration for this report type (product)

![Componen Diagramm](/docs/components.png)

## workflow of the report creation process
1. Report order of product X sent via API to Report-Order-Manager
   1.	Report order saved in Report-Order-Manager
   1.	Report order event sent to corresponding topic
1. Corresponding X-Report-Factory processes report order
   1.	Report factory starts processing the report
   1.	Report factory sends order status updates 
   1.	Report factory sends produced report in XML format to report topic
1. Report-Repository saves the report for later retrieval
    1. Saves xml file in database
    1. Sends event that file has been saved
1. (Optional) Report-Uploader uploads the report to target system via FTP(S)
    1. Uploads file
    1. Sends notification of file uploaded

![Activity Diagramm](/docs/report_creation_process.png)

## Adherance to the 12 Factor principles:

### 1. Codebase
Each service has its own Git repository (1:1 mapping between code repository and service), so that the code of uServices can be version and released independently from each other. The shared configuration of the system also has its own SCM repository (report-configuration). This way the config is decoupled from the code so that it can be versioned and released independently of the code of a service; this will be discussed more detail below.  
### 2. Dependencies
As each service is a Java project all dependencies are declared in the maven project object model (pom.xml) of the service, which is stored in the root of each service's source repository. The services have no dependencies on any native libraries.
### 3.*Configuration*
This factor recommends to read configuration from environment variables. Yet using environment variables to hold configuration values raises some concerns. First, environment variables of a container instance may be read on the container host, hence it is not suitable to store secrets. Second, as some configuration is also kept in version control it explicitly needs to be injected to the environment, which is not the case in case of configuration properties provided by the platform (i.e. properties of platform provided services). As each service is implemented with the *Spring Cloud* framework, the frameworks abstractions are used to read configuration values in application which makes it possible to access values provided in environment variables, applicaton configuration files and the configuration repository, all with the same uniform API.
### 4. Backing Services
The backing services of the services are:
* RabbitMQ AMQP messaging provider
* SQL databases
* HTTP services of downstream uServices.
The factor recommends to inject the configuration (URL and credentials) as environment variables into the application/service and implement the application so that it can adapt to changes of these config values. Yet, using the service concept of Kubernetes, it is possible to change the actual backing service instance behind the service URL without changing the application configuration. So that the backing service can be changed/replaced w/o changes to application configuration as long as K8S dns based service discovery is used and the credentials of the backing service do not change. Otherwise, the new configuration will be injected to the environment and the upstream service may need to reload the configuration on the fly or restart.
### 5. Build, Release, Run

### 6. Processes
### 7. Port Binding 
### 8. Concurrency
### 9. Disposability
### 10. Dev/Prod Parity
### 11. Logs
### 12. Admin Processes
