<?php

	$start_date=$_POST["startdate"];
	$end_date=$_POST["enddate"];
	$batch_id=$_POST["batchid"];
	$subject_id=$_POST["subjectid"];

	$serverName = "ADMIN-PC"; //serverName\instanceName
	$connectionInfo = array( "Database"=>"StudentAttendanceDB", "UID"=>"haresh", "PWD"=>"haresh@123");
	$conn = sqlsrv_connect( $serverName, $connectionInfo);

	if( $conn ) {
		$sql = "DECLARE @colSheetId nvarchar(max);
				SELECT @colSheetId = STUFF((SELECT ', ' + quotename(concat(AttendanceSheet.AttSheetId,':',AttDate)) FROM AttendanceSheet where ";
	if($start_date!='')
		$sql.="Attdate >= '$start_date' and ";

	if($end_date!='')
		$sql.="Attdate <=  '$end_date' and ";
	$sql.="BatchId=$batch_id and SubjectId=$subject_id FOR XML PATH('')), 1, 2, '');
			DECLARE @qry nvarchar(max);
			SET @qry = N'SELECT StudId,FirstName,LastName, ' + @colSheetId + '
			FROM 
			(
				select Student.StudId,Student.FirstName,Student.LastName,concat(AttendanceSheet.AttSheetId,'':'',AttDate) AttIdDate,Status from AttendanceStudent 
			join AttendanceSheet on AttendanceStudent.AttSheetId=AttendanceSheet.AttSheetId
			join Student on Student.StudId=AttendanceStudent.StudId
			where ";
	if($start_date!='')
		$sql.="Attdate >= ''$start_date'' and ";

	if($end_date!='')
		$sql.="Attdate <=  ''$end_date'' and ";
	$sql.="BatchId=$batch_id and SubjectId=$subject_id 
			) tg pivot (Sum(Status) for tg.AttIdDate in (' + @colSheetId + ')) p
			';
			exec sp_executesql @qry;";


		$stmt = sqlsrv_query( $conn, $sql );
		if( $stmt === false) {
			die( print_r( sqlsrv_errors(), true) );
		}
		$numcolumn=sqlsrv_num_fields($stmt);

		$fieldMetadata=sqlsrv_field_metadata( $stmt);

		// output headers so that the file is downloaded rather than displayed
		header('Content-type: text/csv');
		header('Content-Disposition: attachment; filename="report.csv"');
 
		// do not cache the file
		header('Pragma: no-cache');
		header('Expires: 0');
 
		// create a file pointer connected to the output stream
		$file = fopen('php://output', 'w');
 
		$columnname=array('Student ID','First Name','Last Name');
		for($i = 3; $i < $numcolumn; $i++)  
		{  
				$colname = explode(':', $fieldMetadata[$i]['Name']);
				array_push($columnname,$colname[1]);
		}
		array_push($columnname,'Total : '. ($numcolumn-3));


		
		// send the column headers
		fputcsv($file, $columnname);


		while( $row = sqlsrv_fetch_array( $stmt, SQLSRV_FETCH_NUMERIC) ) {
			$datarow=array($row[0],$row[1],$row[2]);
			$attcount=0;
			for($i = 3; $i < $numcolumn; $i++) { 
				$attcount+=$row[$i];
				array_push($datarow,$row[$i]);
			}
			array_push($datarow,$attcount);
			fputcsv($file,$datarow);
			
		}
		exit();
	}else{
		echo "Connection could not be established.<br />";
		die( print_r( sqlsrv_errors(), true));
	}
?>