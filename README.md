# Basic-SpringAPI
Basic Spring API with JPA-Hibernate-Security

For Using this template: This Spring boot template wont run out of the box, you have "2 main options":

```
adding @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}) ontop of the starting class. This will disable a datasource and no database will have to be specified
adding a source in the resources/application.yml file. for an example:
#spring:
#  datasource:
#    url: "jdbc:mysql://localhost:3306/test"
#    username: "root"
#    password:
in the application.properties you can also put in a server port : 
# server:
#    port: 8081
```

## .env file
For security reasons the .env file is not pushed to GitHub.
For the app to work locally:
```
1. Create file .env in the root directory
2. Make sure you do not assign type to it(the app automatically recognizes it) 
3. Copy the contents of .envtemplate into the file you created
4. Enter values to each environment variable
5. You are good to go
```

_Note: if you add any new variables in the .env, please update the template_
