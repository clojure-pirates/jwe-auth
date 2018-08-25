# jwe-auth

Basic example with:
- buddy-auth
- mount
- pedestal

## Usage


- Export the `PORT` environment variable 

```sh
export PORT=3000
```

- Start the server with 

```
lein run 
```

- Test the APIs by using `HTTPie` examples below

```sh
$ http -v :3000/login username=admin password=secret
HTTP/1.1 200 OK
...
{
    "token": "xxx"
}

$ http :3000/admin Authorization:"Token xxx"
HTTP/1.1 200 OK

$ http -v :3000/login username=test password=secret
HTTP/1.1 200 OK
...
{
    "token": "yyy"
}

$ http :3000/author/test/comment comment=foo Authorization:"Token yyy"
HTTP/1.1 200 OK
{
    "msg": "foo"
}
```
## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
