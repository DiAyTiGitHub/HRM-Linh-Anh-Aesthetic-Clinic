import { Paper, Grid, Typography, Table, TableBody, TableCell, TableRow, Divider } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";

const FooterEvaluationCandidateForm = () => {
    const { values } = useFormikContext();

    return (
        <Paper elevation={2} style={{ padding: 20 }}>
            <Table style={{ marginBottom: 20 }}>
                <TableBody>
                    <TableRow>
                        <TableCell style={{ width: "30%", fontWeight: "bold" }}>Nội dung</TableCell>
                        <TableCell style={{ width: "35%", fontWeight: "bold" }}>Nguyện vọng của ứng viên</TableCell>
                        <TableCell style={{ width: "35%", fontWeight: "bold" }}>Người phỏng vấn đề nghị</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Chức danh</TableCell>
                        <TableCell>
                            <GlobitsTextField name='candidateJobTitle' size='small' fullWidth />
                        </TableCell>
                        <TableCell>
                            <GlobitsTextField name='interviewerJobTitle' size='small' fullWidth />
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Mức lương mong muốn</TableCell>
                        <TableCell>
                            <GlobitsVNDCurrencyInput name='candidateExpectedSalary' size='small' fullWidth />
                        </TableCell>
                        <TableCell>
                            <GlobitsVNDCurrencyInput name='interviewerExpectedSalary' size='small' fullWidth />
                        </TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell>Thời gian bắt đầu nhận việc</TableCell>
                        <TableCell>
                            <GlobitsDateTimePicker name='candidateStartWorkingDate' />
                        </TableCell>
                        <TableCell>
                            <GlobitsDateTimePicker name='interviewerStartWorkingDate' />
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>

            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Typography>
                        <strong>Ghi chú:</strong>
                    </Typography>
                    <GlobitsTextField name='note' multiline rows={3} fullWidth placeholder='Nhập ghi chú...' />
                </Grid>
                <Grid item xs={12}>
                    {/* <Typography>
                        <strong>Người phỏng vấn:</strong> {interviewer?.displayName || "N/A"},
                        <strong> Chức danh:</strong> {interviewer?.positionTitle?.name || "N/A"},<strong> Ngày:</strong>{" "}
                        {new Date().toLocaleDateString()}
                    </Typography> */}
                </Grid>
            </Grid>
        </Paper>
    );
};

export default observer(FooterEvaluationCandidateForm);
