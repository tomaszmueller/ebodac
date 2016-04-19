SELECT DATE(sendDate) as 'date', count(EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.id) as 'totalAmount',
0 as 'totalPending', SUM(if(receivedDate is null, 1, 0)) as 'totalFailed', SUM(if(receivedDate is not null, 1, 0)) as 'totalSucceed',
SUM(if(gender = 'Male', 1, 0)) as 'sendToMen', SUM(if(gender = 'Male' AND receivedDate is not null, 1, 0)) as 'successfulSendToMen',
SUM(if(gender = 'Female', 1, 0)) as 'sendToWomen', SUM(if(gender = 'Female' AND receivedDate is not null, 1, 0)) as 'successfulSendToWomen'
FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id
WHERE sendDate >= :minDate AND sendDate <= :maxDate GROUP BY date