Especificaciones tecnicas del proyecto 
Java 17
Spring Boot 3.3.0
Maven 3.0
Mysql 8.0
Prerequisites
--Mysql 8.0 --Maven 3.0 --JDK 21 --Text editor (VsCode) --SDK
Tambein compatible en ambite local con las base de datos H2 de Java 
Install
Para instlar el Api de forma local se debe:

Clone the repo

https://github.com/sebassuaza98/banco.git
Install packages Una vez clonado el repositorio, ejecutamos en el proyecto el comando, para cargar dep:

mvn clean install

Getting Started
Si se desea ejecutar el api de forma local se deber ejecutar el siguiente comando en la consola del proyecto:
El puerto de ejecuccion puede ser el 8080 o el 8000.

.\mvnw.cmd spring-boot:run 

Test
Para ejecutar las pruebas , se escribe el siguiente comando en consola : mvn test
