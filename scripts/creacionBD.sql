Create table usuarios
(
	nombre varchar(60) not null primary key,
	password varchar(60) not null
);

Create table friends
(
	user1 varchar(60) not null,
	user2 varchar(60) not null,
	estado varchar(60) default 'Pendiente',
	primary key(user1,user2),
	foreign key(user1) references usuarios(nombre)
		on delete cascade on update cascade,
	foreign key(user2) references usuarios(nombre)
		on delete cascade on update cascade
);

