#!/bin/sh

psql << EOF
create database aneeque_db;
create user aneeque with encrypted password 'aneeque';
grant all privileges on database aneeque_db to aneeque;
EOF


#It must be run by a user with permissions in PostgreSQL
# to create databases and manage user roles.