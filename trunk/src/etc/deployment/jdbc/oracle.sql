drop table OS_PROPERTYENTRY cascade constraints;
drop table OS_USER cascade constraints;
drop table OS_GROUP cascade constraints;
drop table OS_MEMBERSHIP cascade constraints;
drop table OS_WFENTRY cascade constraints;
drop table OS_CURRENTSTEP cascade constraints;
drop table OS_HISTORYSTEP cascade constraints;
drop table OS_CURRENTSTEP_PREV cascade constraints;
drop table OS_HISTORYSTEP_PREV cascade constraints;
drop sequence seq_os_wfentry;
drop sequence seq_os_currentsteps;

create table OS_PROPERTYENTRY
(
	GLOBAL_KEY varchar(255),
	ITEM_KEY varchar(255),
	ITEM_TYPE smallint,
	STRING_VALUE varchar(255),
	DATE_VALUE date,
	DATA_VALUE blob,
	FLOAT_VALUE float,
	NUMBER_VALUE numeric,
	primary key (GLOBAL_KEY, ITEM_KEY)
);

create table OS_USER
(
    USERNAME varchar(20),
    PASSWORDHASH varchar(2024),
    primary key (USERNAME)
);

create table OS_GROUP
(
    GROUPNAME varchar(20),
    primary key (GROUPNAME)
);

create table OS_MEMBERSHIP
(
    USERNAME varchar(20),
    GROUPNAME varchar(20),
    primary key (USERNAME, GROUPNAME),
    foreign key (USERNAME) references OS_USER(USERNAME),
    foreign key (GROUPNAME) references OS_GROUP(GROUPNAME)
);

create table OS_WFENTRY
(
    ID number,
    NAME varchar(20),
    STATE integer,
    primary key (ID)
);

create table OS_CURRENTSTEP
(
    ID number,
    ENTRY_ID number,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(20),
    START_DATE date,
    FINISH_DATE date,
    DUE_DATE date,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID),
    foreign key (OWNER) references OS_USER(USERNAME),
    foreign key (CALLER) references OS_USER(USERNAME)
);

create table OS_HISTORYSTEP
(
    ID number,
    ENTRY_ID number,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(20),
    START_DATE date,
    FINISH_DATE date,
    DUE_DATE date,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID),
    foreign key (OWNER) references OS_USER(USERNAME),
    foreign key (CALLER) references OS_USER(USERNAME)
);

create table OS_CURRENTSTEP_PREV
(
    ID number,
    PREVIOUS_ID number,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

create table OS_HISTORYSTEP_PREV
(
    ID number,
    PREVIOUS_ID number,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

create sequence seq_os_wfentry minvalue 10 increment by 10;
create sequence seq_os_currentsteps;
