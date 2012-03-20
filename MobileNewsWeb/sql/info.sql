
select sdo_geom.sdo_distance(a.location, b.location, 1, 'unit=METER') from article a, article b;

select this_.title as y0_, this_.location as y1_, SDO_GEOM.SDO_DISTANCE(this_.location, MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-87.527779,38.67301,null),null,null) , 1.0, 'METER') distance from Article this_ where SDO_WITHIN_DISTANCE(this_.location,MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-85.157387,41.185081,null),null,null),'unit=METER distance=5000.0') = 'TRUE';


select this_.title as y0_, this_.location as y1_ from Article this_ where SDO_WITHIN_DISTANCE(this_.location,MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-85.157387,41.185081,null),null,null),'unit=METER distance=5000.0') = 'TRUE';

select this_.title as y0_, this_.location as y1_, SDO_GEOM.SDO_DISTANCE(location, MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-85.157387,41.185081,null),null,null) , 1, 'unit=METER') from content this_ where SDO_WITHIN_DISTANCE(this_.location,MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-87.527779,38.67301,null),null,null),'unit=METER distance=50000.0') = 'TRUE'

select c.title, s.title from content c, content s where SDO_WITHIN_DISTANCE(c.location, s.location,'unit=METER distance=50000.0') = 'TRUE' order by c.title;
select count(*) from story;
select count(*) from story where centroid >= 1301005109877;
select avg(centroid) from story;
select avg(lastPublished) from content;
select this_.title as y0_, this_.location as y1_, SDO_GEOM.SDO_DISTANCE(this_.location, MDSYS.SDO_GEOMETRY(2001,4326,MDSYS.SDO_POINT_TYPE(-85.128859,41.130604,null),null,null) , 1.0, 'unit=METER') distance from Article this_

select count(*) from content group by source_id;
select count(*) from content group by cluster_id;
select count(*), story.title from content join story on content.cluster_id = story.id group by story.title;
select count(*) from term;
select location, feed.title from feed, article where article.source_id = feed.id;
select count(a.id) from feed a;
select * from USER_SDO_GEOM_METADATA;

select * from content where title like '%Rayth%';

select count(*) from term;

select count(*) from content where cluster_id is not null;

select count(*) from clusterterm where tfidf = 0 and rownum < 8000;