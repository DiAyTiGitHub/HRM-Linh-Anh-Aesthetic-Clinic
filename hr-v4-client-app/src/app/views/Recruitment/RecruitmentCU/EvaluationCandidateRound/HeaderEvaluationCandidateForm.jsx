import { Grid, Paper, Typography } from "@material-ui/core";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";

const HeaderEvaluationCandidateForm = () => {
    const { values } = useFormikContext();
    const candidateRecruitmentRound = values?.candidateRecruitmentRound;
    const candidate = candidateRecruitmentRound?.candidate;

    return (
        <Paper elevation={2} style={{ padding: "16px" }}>
            <Typography variant='h5' align='center' style={{ fontWeight: "bold", marginBottom: 20 }}>
                PHIẾU ĐÁNH GIÁ ỨNG VIÊN
            </Typography>

            <Grid container spacing={3}>
                <Grid item xs={4}>
                    <Typography>
                        <strong>Họ và tên ứng viên:</strong> {candidate?.displayName || "N/A"}
                    </Typography>
                </Grid>
                <Grid item xs={4}>
                    <Typography>
                        <strong>Số điện thoại:</strong> {candidate?.phoneNumber || "N/A"}
                    </Typography>
                </Grid>
                <Grid item xs={4}>
                    <Typography>
                        <strong>Chức danh dự tuyển:</strong> {candidate?.positionTitle?.name || "N/A"}
                    </Typography>
                </Grid>
                <Grid item xs={8}>
                    <Typography>
                        <strong>Đơn vị tuyển dụng:</strong> {candidate?.organization?.name || "N/A"}
                    </Typography>
                </Grid>
                <Grid item xs={4}>
                    <Typography>
                        <strong>Phòng ban:</strong> {candidate?.department?.name || "N/A"}
                    </Typography>
                </Grid>
            </Grid>

            {/* <Typography variant='h6' style={{ marginTop: 20, marginBottom: 10 }}>
                KẾT QUẢ KIỂM TRA (*BÀI TEST - nếu có*)
            </Typography>

            <Table style={{ marginBottom: 20 }}>
                <TableBody>
                    <TableRow>
                        <TableCell style={{ width: "30%", fontWeight: "bold" }}>Nghiệp vụ 1:</TableCell>
                        <TableCell style={{ width: "70%" }}>/điểm</TableCell>
                    </TableRow>
                    <TableRow>
                        <TableCell style={{ fontWeight: "bold" }}>Nghiệp vụ 2:</TableCell>
                        <TableCell>/điểm</TableCell>
                    </TableRow>
                </TableBody>
            </Table> */}
        </Paper>
    );
};

export default observer(HeaderEvaluationCandidateForm);
