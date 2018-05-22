# jwe-auth

Basic example with:
- buddy-auth
- mount
- ring
- compojure

## Usage
```sh
$ http -v post :3000/login username=admin password=secret
HTTP/1.1 200 OK
...
{
    "token": "xxx"
}

$ http :3000 Authorization:"Token xxx"
HTTP/1.1 200 OK
```
## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
