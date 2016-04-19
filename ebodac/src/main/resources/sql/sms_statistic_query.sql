SELECT DATE(sendDate) as 'date', count(EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.id) as 'totalAmount',
SUM(if(smsStatus = 'YES' && smsReceivedDate is null, 1, 0)) as 'totalPending', SUM(if(smsStatus = 'FAIL', 1, 0)) as 'totalFailed',
SUM(if(smsStatus = 'YES' && smsReceivedDate is not null, 1, 0)) as 'totalSucceed',
SUM(if(gender = 'Male', 1, 0)) as 'sendToMen', SUM(if(gender = 'Male' AND smsReceivedDate is not null, 1, 0)) as 'successfulSendToMen',
SUM(if(gender = 'Female', 1, 0)) as 'sendToWomen', SUM(if(gender = 'Female' AND smsReceivedDate is not null, 1, 0)) as 'successfulSendToWomen'
FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id
WHERE smsStatus != 'NO' AND sendDate >= :minDate AND sendDate <= :maxDate GROUP BY date