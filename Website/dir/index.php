<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="../css/bootstrap-datetimepicker.min.css" rel="stylesheet" media="screen">
</head>

<body >

<div class="container">
    <form action="GenerateReport.php" method="post" class="form-horizontal"  role="form">
        <fieldset>
            <legend>Student Attendance Report</legend>
            
			<div class="form-group">
                <label for="batchname" class="col-md-2 control-label">Batch Name</label>
                <div class="input-group col-md-5" >
					<?php
						$serverName = "ADMIN-PC"; //serverName\instanceName
						$connectionInfo = array( "Database"=>"StudentAttendanceDB", "UID"=>"haresh", "PWD"=>"haresh@123");
						$conn = sqlsrv_connect( $serverName, $connectionInfo);

						if( $conn ) {
							echo '<select class="form-control" name="batchname">';
							if(($result = sqlsrv_query($conn,"SELECT BatchId,BatchName FROM Batch")) !== false){
								while( $obj = sqlsrv_fetch_object( $result )) {
								echo '<option value="'. $obj->BatchId.'">'. $obj->BatchName.'</option>';
								}
							}
							echo "</select>";	
						}else{
							echo "Connection could not be established.<br />";
							die( print_r( sqlsrv_errors(), true));
						}
					?>
                </div>
            </div>
			<br/>
			<div class="form-group">
                <label for="subjectname" class="col-md-2 control-label">Subject Name</label>
                <div class="input-group col-md-5" >
                    <?php
						$serverName = "ADMIN-PC"; //serverName\instanceName
						$connectionInfo = array( "Database"=>"StudentAttendanceDB", "UID"=>"haresh", "PWD"=>"haresh@123");
						$conn = sqlsrv_connect( $serverName, $connectionInfo);

						if( $conn ) {
							echo '<select class="form-control" name="subjectname">';
							$sql = "SELECT SubId, SubName FROM Subjects";
							$stmt = sqlsrv_query( $conn, $sql );
							if( $stmt === false) {
								die( print_r( sqlsrv_errors(), true) );
							}

							while( $row = sqlsrv_fetch_array( $stmt, SQLSRV_FETCH_NUMERIC) ) {
								echo '<option value="'. $row[0].'">'. $row[1].'</option>';
							}
							echo "</select>";	
						}else{
							echo "Connection could not be established.<br />";
							die( print_r( sqlsrv_errors(), true));
						}
					?>
                </div>
            </div>
			<br/>
			<div class="form-group">
                <label for="startdate" class="col-md-2 control-label">Select Start date</label>
                <div class="input-group date form_date col-md-5" data-date="" data-date-format="dd MM yyyy" data-link-field="startdate" data-link-format="yyyy-mm-dd">
                    <input name="startdate" class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
					<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
            </div>
			<br/>
			<div class="form-group">
                <label for="enddate" class="col-md-2 control-label">Select end date</label>
                <div class="input-group date form_date col-md-5" data-date="" data-date-format="dd MM yyyy" data-link-field="enddate" data-link-format="yyyy-mm-dd">
                    <input name="enddate" class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-remove"></span></span>
					<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                </div>
            </div>
			<br />
			<div class="form-group">
                
                <div class="col-md-5 control-label"><input type="submit" name="submit">
                </div>
            </div>
        </fieldset>
		
    </form>
</div>

<script type="text/javascript" src="./jquery/jquery-1.8.3.min.js" charset="UTF-8"></script>
<script type="text/javascript" src="./bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script type="text/javascript" src="../js/locales/bootstrap-datetimepicker.fr.js" charset="UTF-8"></script>
<script type="text/javascript">
    $('.form_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        showMeridian: 1
    });
	$('.form_date').datetimepicker({
        language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		minView: 2,
		forceParse: 0
    });
	$('.form_time').datetimepicker({
        language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 1,
		minView: 0,
		maxView: 1,
		forceParse: 0
    });
</script>


</body>
</html>
