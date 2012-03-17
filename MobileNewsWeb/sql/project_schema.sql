drop index content_loc;
delete from USER_SDO_GEOM_METADATA where table_name = 'CONTENT';
INSERT INTO USER_SDO_GEOM_METADATA
VALUES (
'CONTENT',
'LOCATION',
SDO_DIM_ARRAY(
SDO_DIM_ELEMENT('Longitude', -180, 180, 5), -- 5 meters tolerance
SDO_DIM_ELEMENT('Latitude', -90, 90, 5) -- 5 meters tolerance
),
4326 -- SRID for 'Longitude / Latitude (WGS 84)' coordinate system
);
create index content_loc on content (location) indextype is MDSYS.SPATIAL_INDEX PARAMETERS ('layer_gtype="POINT"');

drop index cluster_loc;
delete from USER_SDO_GEOM_METADATA where table_name = 'STORY';
INSERT INTO USER_SDO_GEOM_METADATA
VALUES (
'STORY',
'LOCATION',
SDO_DIM_ARRAY(
SDO_DIM_ELEMENT('Longitude', -180, 180, 5), -- 5 meters tolerance
SDO_DIM_ELEMENT('Latitude', -90, 90, 5) -- 5 meters tolerance
),
4326 -- SRID for 'Longitude / Latitude (WGS 84)' coordinate system
);
create index cluster_loc on story (location) indextype is MDSYS.SPATIAL_INDEX PARAMETERS ('layer_gtype="POINT"');

set escape \;
delete from feed;
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Wane 1', 0, 'http://www.wane.com/feeds/rssFeed?siteId=20009\&obfType=RSS_FEED\&categoryId=20000', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Wane 1', 0, 'http://www.wane.com/feeds/rssFeed?siteId=20009\&obfType=RSS_FEED\&categoryId=10001', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Wane 1', 0, 'http://www.wane.com/feeds/rssFeed?siteId=20009\&obfType=RSS_FEED\&categoryId=20016', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Wane 2', 0, 'http://www.wane.com/feeds/rssFeed?siteId=20009\&obfType=RSS_FEED\&categoryId=30384', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'JC', 0, 'http://www.jconline.com/apps/pbcs.dll/section?category=news%25\&template=rss_gd', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Purdue Eng', 0, 'http://www.purdue.edu/newsroom/rss/engineering.xml', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'Earthpublisher', 1, 'http://www.earthpublisher.com/georss.php', 0, 0, 0, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'USGS Earth Quakes', 1, 'http://earthquake.usgs.gov/earthquakes/catalogs/eqs7day-M2.5.xml', 0, 0, 0, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'INC', 0, 'http://www.indianasnewscenter.com/news/local/index.rss2', 0, 0, 1, 0, 0);
INSERT INTO FEED (ID, version, LASTSCANNED, title, GEORSS, URL, LASTPUBLISHED, ERRORCOUNT, enabled, created, updated) VALUES (USER57504.moblie_news_seq.nextval, 0, 0, 'News Sent', 0, 'http://www.news-sentinel.com/apps/pbcs.dll/section?template=RSS\&cat=NEWS\&mime=xml', 0, 0, 1, 0, 0);


delete from statistics;
-- Cluster cutoff cluster distance of 50KM, time cutoff of 6 days, similiarity of .45, 25 initial article terms are used to find potential clusters
insert into statistics (ID, version, articlecount, locationcutoffdistance, maxclustertimecentroid, cutoffdistance, timeattenuationconstant, maxterms, maxtopterms) values(0, 0, 0, 50000, 1000 * 60 * 60 * 24 * 6, .45, 0, 255, 10);
commit;

