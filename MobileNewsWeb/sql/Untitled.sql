
update feed set lastscanned = 0, lastmodified = null, etag = null;
update statistics set articlecount = 0;
delete from content;
delete from term;
delete from clusterterm;
delete from story;
commit;
