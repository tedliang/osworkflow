if exists (select * from dbo.sysobjects where id = object_id(N'[OS_PROPERTYENTRY]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_PROPERTYENTRY]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[OS_CURRENTSTEP_PREV]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_CURRENTSTEP_PREV]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[OS_HISTORYSTEP_PREV]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_HISTORYSTEP_PREV]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[OS_CURRENTSTEP]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_CURRENTSTEP]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[OS_HISTORYSTEP]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_HISTORYSTEP]
GO
if exists (select * from dbo.sysobjects where id = object_id(N'[OS_WFENTRY]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [OS_WFENTRY]
GO

create table OS_WFENTRY
(
    ID int,
    NAME varchar(128),
    STATE smallint,
    primary key (ID)
);

create table OS_CURRENTSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint,
    OWNER varchar(20),
    START_DATE datetime,
    FINISH_DATE datetime,
    DUE_DATE datetime,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)

);

create table OS_HISTORYSTEP
(
    ID int,
    ENTRY_ID int,
    STEP_ID smallint,
    ACTION_ID smallint,
    OWNER varchar(20),
    START_DATE datetime,
    FINISH_DATE datetime,
    DUE_DATE datetime,
    STATUS varchar(20),
    CALLER varchar(20),
    primary key (ID),
    foreign key (ENTRY_ID) references OS_WFENTRY(ID)
);

create table OS_CURRENTSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_CURRENTSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

create table OS_HISTORYSTEP_PREV
(
    ID int,
    PREVIOUS_ID int,
    primary key (ID, PREVIOUS_ID),
    foreign key (ID) references OS_HISTORYSTEP(ID),
    foreign key (PREVIOUS_ID) references OS_HISTORYSTEP(ID)
);

CREATE TABLE OS_PROPERTYENTRY
(
  GLOBAL_KEY varchar(255),
  ITEM_KEY varchar(255),
  ITEM_TYPE smallint,
  STRING_VALUE varchar(255),
  DATE_VALUE datetime,
  DATA_VALUE varbinary(2000),
  FLOAT_VALUE float,
  NUMBER_VALUE numeric,
  primary key (GLOBAL_KEY, ITEM_KEY)
);



