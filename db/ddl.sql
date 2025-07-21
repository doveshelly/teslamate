create table public.schema_migrations
(
    version     bigint not null
        primary key,
    inserted_at timestamp(0)
);

alter table public.schema_migrations owner to teslamate;

create table public.addresses
(
    id             integer default nextval('addresses_id_seq'::regclass) not null
        primary key,
    display_name   varchar(512),
    latitude       numeric(8, 6),
    longitude      numeric(9, 6),
    name           varchar(255),
    house_number   varchar(255),
    road           varchar(255),
    neighbourhood  varchar(255),
    city           varchar(255),
    county         varchar(255),
    postcode       varchar(255),
    state          varchar(255),
    state_district varchar(255),
    country        varchar(255),
    raw            jsonb,
    inserted_at    timestamp(0)                                          not null,
    updated_at     timestamp(0)                                          not null,
    osm_id         bigint,
    osm_type       text
);

alter table public.addresses owner to teslamate;

create unique index addresses_osm_id_osm_type_index
    on public.addresses (osm_id, osm_type);

create table public.tokens
(
    id          integer default nextval('tokens_id_seq'::regclass) not null
        primary key,
    inserted_at timestamp(0)                                       not null,
    updated_at  timestamp(0)                                       not null,
    refresh     bytea,
    access      bytea
);

alter table public.tokens owner to teslamate;

create table public.settings
(
    id                  bigserial
        primary key,
    inserted_at         timestamp(0) not null,
    updated_at          timestamp(0) not null,
    unit_of_length      unit_of_length      default 'km'::unit_of_length     not null,
    unit_of_temperature unit_of_temperature default 'C'::unit_of_temperature not null,
    preferred_range     range               default 'rated'::range           not null,
    base_url            varchar(255),
    grafana_url         varchar(255),
    language            text                default 'en'::text               not null,
    unit_of_pressure    unit_of_pressure    default 'bar'::unit_of_pressure  not null
);

alter table public.settings owner to teslamate;

create table public.geofences
(
    id            integer      default nextval('geofences_id_seq'::regclass) not null
        primary key,
    name          varchar(255)                                               not null,
    latitude      numeric(8, 6)                                              not null,
    longitude     numeric(9, 6)                                              not null,
    radius        smallint     default 25                                    not null,
    inserted_at   timestamp(0)                                               not null,
    updated_at    timestamp(0)                                               not null,
    cost_per_unit numeric(6, 4),
    session_fee   numeric(6, 2),
    billing_type  billing_type default 'per_kwh'::billing_type               not null
);

alter table public.geofences owner to teslamate;

create table public.car_settings
(
    id                     bigserial
        primary key,
    suspend_min            integer default 21    not null,
    suspend_after_idle_min integer default 15    not null,
    req_not_unlocked       boolean default false not null,
    free_supercharging     boolean default false not null,
    use_streaming_api      boolean default true  not null,
    enabled                boolean default true  not null,
    lfp_battery            boolean default false not null
);

alter table public.car_settings owner to teslamate;

create table public.cars
(
    id               smallint default nextval('cars_id_seq'::regclass) not null
        primary key,
    eid              bigint                                            not null,
    vid              bigint                                            not null,
    model            varchar(255),
    efficiency       double precision,
    inserted_at      timestamp(0)                                      not null,
    updated_at       timestamp(0)                                      not null,
    vin              text,
    name             text,
    trim_badging     text,
    settings_id      bigint                                            not null
        references public.car_settings
            on delete cascade,
    exterior_color   text,
    spoiler_type     text,
    wheel_type       text,
    display_priority smallint default 1                                not null,
    marketing_name   varchar(255)
);

alter table public.cars owner to teslamate;

create unique index cars_eid_index
    on public.cars (eid);

create unique index cars_vid_index
    on public.cars (vid);

create unique index cars_vin_index
    on public.cars (vin);

create unique index cars_settings_id_index
    on public.cars (settings_id);

create table public.drives
(
    id                   integer default nextval('trips_id_seq'::regclass) not null
        constraint trips_pkey
            primary key,
    start_date           timestamp                                         not null,
    end_date             timestamp,
    outside_temp_avg     numeric(4, 1),
    speed_max            smallint,
    power_max            smallint,
    power_min            smallint,
    start_ideal_range_km numeric(6, 2),
    end_ideal_range_km   numeric(6, 2),
    start_km             double precision,
    end_km               double precision,
    distance             double precision,
    duration_min         smallint,
    car_id               smallint                                          not null
        references public.cars
            on delete cascade,
    inside_temp_avg      numeric(4, 1),
    start_address_id     integer
                                                                           references public.addresses
                                                                               on delete set null,
    end_address_id       integer
                                                                           references public.addresses
                                                                               on delete set null,
    start_rated_range_km numeric(6, 2),
    end_rated_range_km   numeric(6, 2),
    start_position_id    integer,
    end_position_id      integer,
    start_geofence_id    integer
                                                                           references public.geofences
                                                                               on delete set null,
    end_geofence_id      integer
                                                                           references public.geofences
                                                                               on delete set null
);

alter table public.drives owner to teslamate;

create index trips_car_id_index
    on public.drives (car_id);

create index drives_start_position_id_index
    on public.drives (start_position_id);

create index drives_end_position_id_index
    on public.drives (end_position_id);

create index trips_start_address_id_index
    on public.drives (start_address_id);

create index trips_end_address_id_index
    on public.drives (end_address_id);

create index drives_start_geofence_id_index
    on public.drives (start_geofence_id);

create index drives_end_geofence_id_index
    on public.drives (end_geofence_id);

create table public.positions
(
    id                      integer default nextval('positions_id_seq'::regclass) not null
        primary key,
    date                    timestamp                                             not null,
    latitude                numeric(8, 6)                                         not null,
    longitude               numeric(9, 6)                                         not null,
    speed                   smallint,
    power                   smallint,
    odometer                double precision,
    ideal_battery_range_km  numeric(6, 2),
    battery_level           smallint,
    outside_temp            numeric(4, 1),
    elevation               smallint,
    fan_status              integer,
    driver_temp_setting     numeric(4, 1),
    passenger_temp_setting  numeric(4, 1),
    is_climate_on           boolean,
    is_rear_defroster_on    boolean,
    is_front_defroster_on   boolean,
    car_id                  smallint                                              not null
        references public.cars
            on delete cascade,
    drive_id                integer
                                                                                  references public.drives
                                                                                      on delete set null,
    inside_temp             numeric(4, 1),
    battery_heater          boolean,
    battery_heater_on       boolean,
    battery_heater_no_power boolean,
    est_battery_range_km    numeric(6, 2),
    rated_battery_range_km  numeric(6, 2),
    usable_battery_level    smallint,
    tpms_pressure_fl        numeric(4, 1),
    tpms_pressure_fr        numeric(4, 1),
    tpms_pressure_rl        numeric(4, 1),
    tpms_pressure_rr        numeric(4, 1)
);

alter table public.positions owner to teslamate;

alter table public.drives
    add foreign key (start_position_id) references public.positions
        on delete set null;

alter table public.drives
    add foreign key (end_position_id) references public.positions
        on delete set null;

create index positions_date_index
    on public.positions (date);

create index positions_car_id_index
    on public.positions (car_id);

create index positions_drive_id_date_index
    on public.positions (drive_id, date);

create index "positions_car_id_date__ideal_battery_range_km_IS_NOT_NULL_index"
    on public.positions (car_id, date, (ideal_battery_range_km IS NOT NULL))
    where
    (ideal_battery_range_km IS NOT NULL);

create table public.states
(
    id         integer default nextval('states_id_seq'::regclass) not null
        primary key,
    state      states_status                                      not null,
    start_date timestamp                                          not null,
    end_date   timestamp,
    car_id     smallint                                           not null
        references public.cars
            on delete cascade,
    constraint positive_duration
        check (end_date >= start_date)
);

alter table public.states owner to teslamate;

create unique index "states_car_id__end_date_IS_NULL_index"
    on public.states (car_id, (end_date IS NULL))
    where
    (end_date IS NULL);

create index states_car_id_index
    on public.states (car_id);

create table public.charging_processes
(
    id                   integer default nextval('charging_processes_id_seq'::regclass) not null
        primary key,
    start_date           timestamp                                                      not null,
    end_date             timestamp,
    charge_energy_added  numeric(8, 2),
    start_ideal_range_km numeric(6, 2),
    end_ideal_range_km   numeric(6, 2),
    start_battery_level  smallint,
    end_battery_level    smallint,
    duration_min         smallint,
    outside_temp_avg     numeric(4, 1),
    car_id               smallint                                                       not null
        references public.cars
            on delete cascade,
    position_id          integer                                                        not null
        references public.positions,
    address_id           integer
                                                                                        references public.addresses
                                                                                            on delete set null,
    start_rated_range_km numeric(6, 2),
    end_rated_range_km   numeric(6, 2),
    geofence_id          integer
                                                                                        references public.geofences
                                                                                            on delete set null,
    charge_energy_used   numeric(8, 2),
    cost                 numeric(6, 2)
);

alter table public.charging_processes owner to teslamate;

create index charging_processes_car_id_index
    on public.charging_processes (car_id);

create index charging_processes_position_id_index
    on public.charging_processes (position_id);

create index charging_processes_address_id_index
    on public.charging_processes (address_id);

create table public.charges
(
    id                       integer default nextval('charges_id_seq'::regclass) not null
        primary key,
    date                     timestamp                                           not null,
    battery_heater_on        boolean,
    battery_level            smallint,
    charge_energy_added      numeric(8, 2)                                       not null,
    charger_actual_current   smallint,
    charger_phases           smallint,
    charger_pilot_current    smallint,
    charger_power            smallint                                            not null,
    charger_voltage          smallint,
    fast_charger_present     boolean,
    conn_charge_cable        varchar(255),
    fast_charger_brand       varchar(255),
    fast_charger_type        varchar(255),
    ideal_battery_range_km   numeric(6, 2)                                       not null,
    not_enough_power_to_heat boolean,
    outside_temp             numeric(4, 1),
    charging_process_id      integer                                             not null
        references public.charging_processes
            on delete cascade,
    battery_heater           boolean,
    battery_heater_no_power  boolean,
    rated_battery_range_km   numeric(6, 2),
    usable_battery_level     smallint
);

alter table public.charges owner to teslamate;

create index charges_date_index
    on public.charges (date);

create index charges_charging_process_id_index
    on public.charges (charging_process_id);

create table public.updates
(
    id         integer default nextval('updates_id_seq'::regclass) not null
        primary key,
    start_date timestamp                                           not null,
    end_date   timestamp,
    version    varchar(255),
    car_id     smallint                                            not null
        references public.cars
            on delete cascade,
    constraint positive_duration
        check (end_date >= start_date)
);

alter table public.updates owner to teslamate;

create index updates_car_id_index
    on public.updates (car_id);



