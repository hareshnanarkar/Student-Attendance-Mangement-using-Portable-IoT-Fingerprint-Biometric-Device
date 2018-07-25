<html>
<head>
<style>
table {
  table-layout: fixed; 
  width: 100%;
  *margin-left: -100px; /*ie7*/
}
td, th {
  vertical-align: top;
  border-top: 1px solid #ccc;
  padding: 10px;
  width: 100px;
}
.fix {
  position: auto; /*ie7*/
  margin-left: -100px;
  width: 80px;
}
.outer {
  position: relative;
}
.inner {
  overflow-x: scroll;
  overflow-y: scroll;
  margin-left: 100px;
  height:500px;
}
</style>
</head>
<body>
<form action="Generatecsv.php" method="post">
<h1>Attendance Report</h1>
<hr/>

<input type="submit" name="submit" value="Generate CSV">
<br/>
<div class="inner">
<?php

if(isset($_POST['submit'])){
	$start_date=$_POST["startdate"];
	$end_date=$_POST["enddate"];
	$batch_id=$_POST["batchname"];
	$subject_id=$_POST["subjectname"];
	
	echo '<input type="hidden" name="batchid" value="'.$batch_id.'">';
	echo '<input type="hidden" name="subjectid" value="'.$subject_id.'">';
	echo '<input type="hidden" name="startdate" value="'.$start_date.'">';
	echo '<input type="hidden" name="enddate" value="'.$end_date.'">';
	
	//echo gettype($start_date);
	//echo gettype($end_date);
	//echo $batch_id;
	//echo $subject_id;

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
		echo "\n<br/>\n<table>\n";
		echo "<tr>\n";
		$fieldMetadata=sqlsrv_field_metadata( $stmt);
		echo "<th class=\"fix\">Student ID</th>\n";
		echo "<th>First Name</th>\n";
		echo "<th>Last Name</th>\n";
		for($i = 3; $i < $numcolumn; $i++)  
		{  
				$colname = explode(':', $fieldMetadata[$i]['Name']);
				echo "<th>". $colname[1] . "</th>\n";
		}
		echo "<th>Total (" . ($numcolumn-3) . ")</th>";
		echo "</tr>\n";
		while( $row = sqlsrv_fetch_array( $stmt, SQLSRV_FETCH_NUMERIC) ) {
			echo "<tr>\n";
			echo "<th class=\"fix\">".$row[0]."</th>\n";
			echo "<th>".$row[1]."</th>\n";
			echo "<th>".$row[2]."</th>\n";
			$attcount=0;
			for($i = 3; $i < $numcolumn; $i++) { 
				$attcount+=$row[$i];
				if($row[$i]==0)
					echo "<td style=\"background-color:#F6F6F6;\">".$row[$i]."</td>\n";
				else
					echo "<td>".$row[$i]."</td>\n";
			}
			echo "<td>" . $attcount . "</td>";
			echo "</tr>\n";
			//echo  $row[0].' - '. $row[1].' - '. $row[2].' - '. $row[3];
			//echo "<br/>";
		}
		echo "</table>\n";
	}else{
		echo "Connection could not be established.<br />";
		die( print_r( sqlsrv_errors(), true));
	}
}
else
	header('Location:index.php');

?>
</div>
</form>
</body>
</html>