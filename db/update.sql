--创建gps表，用来存储每次百度地图api根据经纬度查出来的地址，减少调用次数
create table gps
(
    latitude  varchar(50),
    longitude varchar(50),
    address   varchar(512),
    id        integer default nextval('gps_id_seq'::regclass) not null
        constraint gps_pk
            primary key
);
alter table gps owner to teslamate;
create index gps_latitude_longitude_index on gps (latitude, longitude);

--新增推送结果字段，防止重复推送
alter table drives add push_result varchar(2) default '00';
create index drives_push_result_index on drives (push_result);