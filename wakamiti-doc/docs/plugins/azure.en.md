---
title: Azure Integration
date: 2023-07-04
slug: /en/plugins/azure
---

This plugin integrates the results of a Wakamiti execution with an existing
[Azure](https://azure.microsoft.com/) test plan, being able to attach files (such as the 
one generated by the HTML Reporter plugin) to the Azure run.

In order to notify the results, the following conditions must be satisfied:
- The scenario must be tagged with an specific tag (by default, `@Azure`)
- The scenario msut have defined the properties `azurePlan`, `azureSuite`, and `azureTest`,
valued with the plan name, the suite name, and the test case name that are defined in the Azure
project.

Test cases not satisfying this criteria will be ignored.

In case the Wakamiti plan includes test cases from several Azure test plans, the plugin will create 
a diferent run for each one of them.


## Configuration


---
####  `azure.host`
The host address where the Azure server is located.

Example:

```yaml
azure:
  host: azure.mycompany.org
  
```

---
####  `azure.credentials.user`
Username to be used with the Azure REST API, passed as a HTTP basic authentication.

Example:

```yaml
azure:
  credentials:
    user: myuser

```


---
####  `azure.credentials.password`
Password to be used with the Azure REST API, passed as a HTTP basic authentication.

Example:

```yaml
azure:
  credentials:
    password: xKHJFHLKJ7897
  
```


---
####  `azure.apiVersion`
The Azure REST API version to be used to send the notifications.

Default value is `5.0-preview`.

Example:

```yaml
azure:
  apiVersion: '6.0-preview'
  
```


---
####  `azure.organization`
The Azure organization owning the test plan.

Example:

```yaml
azure:
  organization: MyOrganization
  
```


---
####  `azure.project`
The Azure project owning of the test plan.

Example:

```yaml
azure:
  project: MyProject
  
```


---
####  `azure.tag`
The tag to be used to decide if the Azure integration should be applied.

Default value is `Azure`.

Example:

```yaml
azure:
  tag: AzureExecution
  
```



---
####  `azure.attachments`
A list of files, o filename patterns in _glob_ format, that would be attached to the Azure run.


Example:

```yaml
azure:
  attachments:
    - 'wakamiti.html'
    - '*.json'  
```