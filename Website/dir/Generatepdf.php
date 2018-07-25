<?php
//============================================================+
// File name   : example_006.php
// Begin       : 2008-03-04
// Last Update : 2013-05-14
//
// Description : Example 006 for TCPDF class
//               WriteHTML and RTL support
//
// Author: Nicola Asuni
//
// (c) Copyright:
//               Nicola Asuni
//               Tecnick.com LTD
//               www.tecnick.com
//               info@tecnick.com
//============================================================+

/**
 * Creates an example PDF TEST document using TCPDF
 * @package com.tecnick.tcpdf
 * @abstract TCPDF - Example: WriteHTML and RTL support
 * @author Nicola Asuni
 * @since 2008-03-04
 */

// Include the main TCPDF library (search for installation path).
require_once('tcpdf_include.php');
if(!isset($_POST['submit'])){
}
else
{
// create new PDF document
$pdf = new TCPDF(PDF_PAGE_ORIENTATION, PDF_UNIT, PDF_PAGE_FORMAT, true, 'UTF-8', false);

// set document information
$pdf->SetCreator(PDF_CREATOR);
$pdf->SetAuthor('Nicola Asuni');
$pdf->SetTitle('TCPDF Example 006');
$pdf->SetSubject('TCPDF Tutorial');
$pdf->SetKeywords('TCPDF, PDF, example, test, guide');

// set default header data
$pdf->SetHeaderData(PDF_HEADER_LOGO, PDF_HEADER_LOGO_WIDTH, PDF_HEADER_TITLE.' 006', PDF_HEADER_STRING);

// set header and footer fonts
$pdf->setHeaderFont(Array(PDF_FONT_NAME_MAIN, '', PDF_FONT_SIZE_MAIN));
$pdf->setFooterFont(Array(PDF_FONT_NAME_DATA, '', PDF_FONT_SIZE_DATA));

// set default monospaced font
$pdf->SetDefaultMonospacedFont(PDF_FONT_MONOSPACED);

// set margins
$pdf->SetMargins(PDF_MARGIN_LEFT, PDF_MARGIN_TOP, PDF_MARGIN_RIGHT);
$pdf->SetHeaderMargin(PDF_MARGIN_HEADER);
$pdf->SetFooterMargin(PDF_MARGIN_FOOTER);

// set auto page breaks
$pdf->SetAutoPageBreak(TRUE, PDF_MARGIN_BOTTOM);

// set image scale factor
$pdf->setImageScale(PDF_IMAGE_SCALE_RATIO);

// set some language-dependent strings (optional)
if (@file_exists(dirname(__FILE__).'/lang/eng.php')) {
    require_once(dirname(__FILE__).'/lang/eng.php');
    $pdf->setLanguageArray($l);
}

// ---------------------------------------------------------

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// Print a table

// test custom bullet points for list

// add a page
$pdf->AddPage();

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
//echo "<br/>\n" . $sql . "<br/>\n";
		$stmt = sqlsrv_query( $conn, $sql );
		if( $stmt === false) {
			die( print_r( sqlsrv_errors(), true) );
		}
		$numcolumn=sqlsrv_num_fields($stmt);


$html="<html>
<head>
<style>
table, th, td {
    border: 3px solid black;
    border-collapse: collapse;
}
th, td {
	border: 1px solid black;
    padding: 5px;
    text-align: left;
}
</style>
</head>
<body>";

		$html.= "\n<br/>\n<table border=\"1\">\n";
		$html.= "<tr>\n";
		$fieldMetadata=sqlsrv_field_metadata( $stmt);
		$html.= "<th>Student ID</th>\n";
		$html.= "<th>First Name</th>\n";
		$html.= "<th>Last Name</th>\n";
		for($i = 3; $i < $numcolumn; $i++)  
		{  
				$colname = explode(':', $fieldMetadata[$i]['Name']);
				$html.= "<th>". $colname[1] . "</th>\n";
		}
		$html.= "<th>Total (" . ($numcolumn-3) . ")</th>";
		$html.= "</tr>\n";
		while( $row = sqlsrv_fetch_array( $stmt, SQLSRV_FETCH_NUMERIC) ) {
			$html.= "<tr>\n";
			$html.= "<td>".$row[0]."</td>\n";
			$html.= "<td>".$row[1]."</td>\n";
			$html.= "<td>".$row[2]."</td>\n";
			$attcount=0;
			for($i = 3; $i < $numcolumn; $i++) { 
				$attcount+=$row[$i];
				$html.= "<td>".$row[$i]."</td>\n";
			}
			$html.= "<td>" . $attcount . "</td>";
			$html.= "</tr>\n";
			//echo  $row[0].' - '. $row[1].' - '. $row[2].' - '. $row[3];
			//echo "<br/>";
		}
		$html.= "</table>\n";
		$html.="</body></html>";
	}else{
		echo "Connection could not be established.<br />";
		die( print_r( sqlsrv_errors(), true));
	}


// output the HTML content
$pdf->writeHTML($html, true, false, true, false, '');

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

// reset pointer to the last page
$pdf->lastPage();

// ---------------------------------------------------------

//Close and output PDF document
$pdf->Output('example_006.pdf', 'I');

//============================================================+
// END OF FILE
//============================================================+
}
?>