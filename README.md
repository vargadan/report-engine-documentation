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

Adherance to the 12 Factor principles:
1. Codebase
1. Dependencies
1. Config
1. Backing Services
1. Build, Release, Run
1. Processes
1. Port Binding 
1. Concurrency
1. Disposability
1. Dev/Prod Parity
1. Logs
1. Admin Processes
