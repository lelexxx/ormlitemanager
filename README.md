OrmLiteManager
==============

Stand alone classes to manage sqlite database with the "ORMLite library" on Android apps.

Features
========

	- Create, insert, select, update, delete objects
	- Use EQUAL, NOTEQUAL, LIKE, NOTLIKE operators in conditions (WHERE statement)
	- Use AND, OR operators to select datas
	
TO-DO
=====

	- Methods could use List<WhereCondition> 
	- Normalize comments

How to install
==============

Copy and paste "libs" content (2 jars, ORMLite libraries) in your libs folder.
Add as library the 2 jars.

Copy and paste "database" folder in your project.
Modify package name and import in order to correspond to your project.
In "DataAccessLayer.java" file, Modify "DATABASE_NAME" and "DATABASE_VERSION" member values.

SEE
===

OrmLiteMamanger use ormlite library : http://ormlite.com/
