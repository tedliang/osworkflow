alter table OS_HISTORYSTEP drop constraint historystep_entry_fk;
alter table OS_CURRENTSTEP drop constraint currentstep_entry_fk;
drop table OS_HISTORYSTEP;
drop table OS_PROPERTYENTRY;
drop table OS_WFENTRY;
drop table OS_CURRENTSTEP;
DROP TABLE hibernate_sequence;
create table OS_HISTORYSTEP (
   id BIGINT not null,
   action_Id INTEGER,
   caller VARCHAR(255),
   finish_Date TIMESTAMP,
   start_Date TIMESTAMP,
   due_Date TIMESTAMP,
   owner VARCHAR(255),
   status VARCHAR(255),
   step_Id INTEGER,
   entry_Id BIGINT,
   stepIndex INTEGER,
   primary key (id)
);
create table OS_PROPERTYENTRY (
   entity_name VARCHAR(125) not null,
   entity_id BIGINT not null,
   entity_key VARCHAR(255) not null,
   key_type INTEGER,
   boolean_val BIT,
   double_val DOUBLE,
   string_val VARCHAR(255),
   long_val BIGINT,
   int_val INTEGER,
   date_val DATE,
   primary key (entity_name, entity_id, entity_key)
);
create table OS_WFENTRY (
   id BIGINT not null,
   name VARCHAR(255),
   state INTEGER,
   primary key (id)
);
create table OS_CURRENTSTEP (
   id BIGINT not null,
   action_Id INTEGER,
   caller VARCHAR(255),
   finish_Date TIMESTAMP,
   start_Date TIMESTAMP,
   due_Date TIMESTAMP,
   owner VARCHAR(255),
   status VARCHAR(255),
   step_Id INTEGER,
   entry_Id BIGINT,
   stepIndex INTEGER,
   primary key (id)
);
alter table OS_HISTORYSTEP add constraint historystep_entry_fk foreign key (entry_Id) references OS_WFENTRY;
alter table OS_CURRENTSTEP add constraint currentstep_entry_fk foreign key (entry_Id) references OS_WFENTRY;
CREATE TABLE hibernate_sequence(
   id NUMERIC
);
