# Simple Spring Boot JPA Example

```
$ curl http://localhost:8080/init && echo
$ curl http://localhost:8080/pets && echo
[{"id":1,"name":"tina","owner":{"id":1,"name":"reddy"}},{"id":2,"name":"nemo","owner":{"id":2,"name":"mathan"}}]
$ curl -X DELETE http://localhost:8080/owners/1 && echo
$ curl http://localhost:8080/pets && echo
[{"id":2,"name":"nemo","owner":{"id":2,"name":"mathan"}}]
$```
