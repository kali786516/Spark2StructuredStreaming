email_end_users()
{

  echo "email end users function"
  status=cat ${TMP}/extract_and_email_$$_${work_flow}.out | grep -i "failure"

ret_code=$?
if [[ ${ret_code} -eq 0 ]]; then

msg="to:${MAILGRP}
subject:JOB SUMMARY [FAILURE]:-JOB RUN SUMMARY FOR WORK_FLOW <${work_flow}> <FAILURE> ENV <${ENV}> from:${MAILSENDER}
MIME-Version: 1.0
Content-Type: text/html
Content-Disposition: inline
<HTML><BODY><PRE>
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
AUDIT DATA:-
`cat ${TMP}/extract_and_email_$$_${work_flow}.out`
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
</PRE></BODY></HTML>"

else

msg="to:${MAILGRP}
subject:JOB SUMMARY [SUCCESS]:-JOB RUN SUMMARY FOR WORK_FLOW <${work_flow}> <SUCCESS> ENV <${ENV}> from:${MAILSENDER}
MIME-Version: 1.0
Content-Type: text/html
Content-Disposition: inline
<HTML><BODY><PRE>
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
AUDIT DATA:-
`cat ${TMP}/extract_and_email_$$_${work_flow}.out`
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
</PRE></BODY></HTML>"

fi


echo "${msg}" | /usr/sbin/sendmail -t

#echo "${msg}" | mailx ${AZ_MAILGRP}

rm -f ${TMP}/extract_and_email_$$_${work_flow}.out

}

##############################################################################################################################


email_alert()
{

  work_flow=$1
  function_name=$2
  status=$3
  script_name=$4
  comp_name=$5
  #log=$6

status=`echo ${status} | tr '[:lower:]' '[:upper:]'`
function_name=`echo ${function_name} | tr '[:lower:]' '[:upper:]'`
work_flow=`echo ${work_flow} | tr '[:lower:]' '[:upper:]'`
script_name=`echo ${script_name} | tr '[:lower:]' '[:upper:]'`
comp_name=`echo ${comp_name} | tr '[:lower:]' '[:upper:]'`
#log==`echo ${log} | tr '[:lower:]' '[:upper:]'`

msg="to:${MAILGRP}
subject:AZ JOB ALERT <${status}> :-JOB RUN SUMMARY FOR WORK_FLOW <${work_flow}> AND COMP_NAME <${comp_name}> AZ_ENV <${ENV}> from:${MAILSENDER}
MIME-Version: 1.0
Content-Type: text/html
Content-Disposition: inline
<HTML><BODY><PRE>
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
NOTE:-
SQL QUERY TO EXTRACT META DATA FOR THIS JOB FAILURE:-

select comp_parm,comp_parm_value from default.inbound_metadata_table where upper(work_flow)=${quote}${work_flow}${quote} and upper(comp_name)=${quote}${comp_name}${quote};

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

ALERT:-

JOB WITH WORK FLOW <${work_flow}> BASH/UNIX SCRIPT NAME <${script_name}> FUNCTION_NAME <${function_name}> AND COMP_NAME <${comp_name}>:- <${status}>

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
</PRE></BODY></HTML>"

echo "${msg}" | /usr/sbin/sendmail -t


}
