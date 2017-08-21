# reporting-engine
sample cloud native application composed of multiple microservices

## services
- Order Manager
- Report Factory
- Report Repository
- Report Uploader
- Report Emailer

## workflow
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
1. (Optional) Report-Emailer emails notification to requester about the report being ready
    1. Sends email 
    1. Sends notification of email-sent status event
1. (Optional) Report-Uploader uploads the report to target system via FTPS
    1. Uploads file
    1. Sends notification of file uploaded
