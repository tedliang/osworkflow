drop table os_propertyentry;
create table OS_PROPERTYENTRY
(
	ID serial primary key,
	GLOBAL_KEY varchar(250),
	ITEM_KEY varchar(250),
	ITEM_TYPE smallint,
	STRING_VALUE varchar(255),
	DATE_VALUE date,
	DATA_VALUE byte,
	FLOAT_VALUE float,
	NUMBER_VALUE decimal
);

drop table os_wfentry;
create table OS_WFENTRY
(
    ID integer,
    NAME varchar(60),
    INITIALIZED integer,
    primary key (ID)
);

drop table os_currentstep;
create table OS_CURRENTSTEP
(
    ID integer,
    ENTRY_ID integer,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(35),
    START_DATE date,
    FINISH_DATE date,
    DUE_DATE date,
    STATUS varchar(40),
	CALLER varchar(35),

    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
);

-- create index os_current_entry on os_currentstep (entry_id);
create index os_current_owner on os_currentstep (owner);
create index os_current_caller on os_currentstep (caller);

drop table os_historystep;
create table OS_HISTORYSTEP
(
    ID integer,
    ENTRY_ID integer,
    STEP_ID integer,
    ACTION_ID integer,
    OWNER varchar(35),
    START_DATE date,
    FINISH_DATE date,
    DUE_DATE date,
    STATUS varchar(40),
    CALLER varchar(35),

    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
);

-- create index os_history_entry on os_historystep (entry_id);
create index os_history_owner on os_historystep (owner);
create index os_history_caller on os_historystep (caller);


drop table OS_CURSTEP_PREV;
create table OS_CURSTEP_PREV
(
    ID integer,
    PREVIOUS_ID integer,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

-- create index os_curstep_prev on os_curstep_prev (previous_id);


create table OS_HISTSTEP_PREV
(
    ID integer,
    PREVIOUS_ID integer,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

-- create index os_historystep_previous on os_historystep_prev (previous_id);
