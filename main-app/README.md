# Main App - MAS

## Create employee with employer and jobs /mas-gateway/create-employee
{"id":null,"name":"Employee1","employerDTOS":[{"employerId":1,"name":"Employer1","jobDTOS":[{"jobId":1,"title":"Job1","workingHours":5},{"jobId":2,"title":"Job2","workingHours":15}]}],"active":true}

SELECT ee.name employee_name, ee.employee_id, e.name employer_name, e.employer_id, j.title, j.job_id, eje.id, eje.working_hours
from employee_db.employee ee
left join employee_db.employee_job_employer eje on eje.employee_id = ee.employee_id
left join employee_db.employer e on e.local_employer_id = eje.employer_id
left join employee_db.job j on j.local_job_id = eje.job_id;


## Create employer with jobs only /mas-gateway/create-employer
{"id":null,"name":"Employer1","jobs":[{"title":"Job1","description":"Employer1","hourRate":10},{"title":"Job1","description":"Employer1","hourRate":10}]}

## Assign employee to employer - job /mas-gateway/assign-employee
{"employerId":1,"employee":{"id":1,"name":"Employee1","active":true},"jobIds":[1,2]}

SELECT e.name, j.title, ee.employee_id, ee.local_employee_id, ee.name
FROM employer_db.employer e
left join employer_db.job j on e.employer_id = j.employer_id
left join employer_db.job_employee je on j.job_id = je.job_id
left join employer_db.employee ee on je.employee_id = ee.local_employee_id;


## Others

### GraphQL - fetch employers from EM, no employees from EEM /graphql/employers
Hours are not determined hence they are null

### GraphQL - fetch employers from EM plus employees from EEM using feign client /graphql/employer/{id}
Hours are obtained from EEM for each employee that is correlated with employer - job

### Get in-memory Kafka messages 
GET /api/messages/received
Messages are published in EEM and a permanent listener logs them automatically; they are also stored in app memory

### Delete in-memory Kafka messages 
DELETE /api/messages/received
Published messages by EEM can be deleted from app memory
