# scalable_bank

Scalable bank
by Dong Long and Meilong Pan
配置：
1.download postgresql and postgresql JDBC jar from https://jdbc.postgresql.org/download.html
2.Add this jar file under your class path
3.set up postgresql database(Highly recommended with pgadmin(download from https://www.pgadmin.org/)):
  a.create a new user name:dl208,password:longdong. Set its permission to superuser, allow to create&delete database
  b.create or set a new data server with port 5432
  c.create a new database named bank
4.set input xml's location & output xml's location in bank_st,main function
5.run bank_st
