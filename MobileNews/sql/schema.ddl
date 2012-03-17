
    create table Article (
        id number(19,0) not null,
        version number(10,0) not null,
        created number(19,0) not null,
        eTag varchar2(1024 char),
        lastModified varchar2(50 char),
        lastPublished number(19,0) not null,
        lastScanned number(19,0) not null,
        title varchar2(500 char),
        updated number(19,0) not null,
        url varchar2(2048 char) not null,
        description long,
        descriptionType varchar2(50 char),
        location MDSYS.SDO_GEOMETRY,
        place_name varchar2(100 char),
        cluster_id number(19,0),
        source_id number(19,0) not null,
        primary key (id),
        unique (url)
    );

    create table ClusterTerm (
        term varchar2(36 char) not null,
        termCount number(10,0) not null,
        tfidf double precision not null,
        version number(10,0) not null,
        cluster_id number(19,0),
        primary key (cluster_id, term)
    );

    create table Feed (
        id number(19,0) not null,
        version number(10,0) not null,
        created number(19,0) not null,
        eTag varchar2(1024 char),
        lastModified varchar2(50 char),
        lastPublished number(19,0) not null,
        lastScanned number(19,0) not null,
        title varchar2(500 char),
        updated number(19,0) not null,
        url varchar2(2048 char) not null,
        enabled number(1,0) not null,
        errorCount number(10,0) not null,
        geoRSS number(1,0) not null,
        primary key (id),
        unique (url)
    );

    create table Statistics (
        id number(19,0) not null,
        version number(10,0) not null,
        articleCount number(10,0) not null,
        locationCutoffDistance double precision not null,
        maxClusterTimeCentoid number(10,0) not null,
        maxTerms number(10,0) not null,
        tfidfCutoffDistance double precision not null,
        timeAttenuationConstant double precision not null,
        primary key (id)
    );

    create table Term (
        term varchar2(36 char) not null,
        termCount number(10,0) not null,
        version number(10,0) not null,
        primary key (term)
    );

    create table story (
        id number(19,0) not null,
        version number(10,0) not null,
        location MDSYS.SDO_GEOMETRY,
        published number(19,0) not null,
        title varchar2(255 char),
        primary key (id)
    );

    create index article_lastpublished on Article (lastPublished);

    alter table Article 
        add constraint FK379164D619E707F1 
        foreign key (source_id) 
        references Feed;

    alter table Article 
        add constraint FK379164D665D4AC66 
        foreign key (cluster_id) 
        references story;

    alter table ClusterTerm 
        add constraint FK1F5EE56665D4AC66 
        foreign key (cluster_id) 
        references story;

    create index cluster_published on story (published);

    create sequence MOBLIE_NEWS_SEQ;
