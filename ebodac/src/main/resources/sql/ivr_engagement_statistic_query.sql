SELECT subjectId, count(subjectId) as 'callsExpected',
count(subjectId) as 'pushedSuccessfully', SUM(if(receivedDate is not null, 1, 0)) as 'received',
SUM(if(messagePercentListened >= 50, 1, 0)) as 'activelyListened', SUM(if(receivedDate is null, 1, 0)) as 'failed'
FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id
GROUP BY subjectId