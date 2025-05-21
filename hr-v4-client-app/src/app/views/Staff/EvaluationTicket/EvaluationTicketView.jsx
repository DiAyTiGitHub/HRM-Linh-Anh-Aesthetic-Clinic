import {
    Box,
    Checkbox,
    FormControlLabel,
    FormGroup,
    Grid,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableRow,
    Typography,
} from "@material-ui/core";
import { observer } from "mobx-react";
import { forwardRef, memo, useEffect } from "react";

import { useStore } from "app/stores";
import { useState } from "react";

import { EVALUATION_STATUS } from "app/LocalConstants";
import { getDate } from "app/LocalFunction";
import ConstantList from "app/appConfig";
import { t } from "app/common/CommonFunctions";
import GlobitsTable from "app/common/GlobitsTable";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import Config from "../../../common/GlobitsConfigConst";
function EvaluationTicketView(props) {
    const { id } = useParams();
    const { data: propsData } = props;
    const [data, setData] = useState({});
    const [row, setRows] = useState([]);

    const { getEvaluationFormsById } = useStore().evaluationTicketStore;
    const { getAllContractType, listContractType } = useStore().contractTypeStore;

    async function handlePrint(ticketId) {
        if (ticketId) {
            try {
                const form = await getEvaluationFormsById(ticketId);
                setData(form);
            } catch (error) {
                console.error("Error fetching evaluation data:", error);
            }
        }
    }

    useEffect(() => {
        getAllContractType();
        // If propsData exists, use it; otherwise fetch by id
        if (propsData) {
            setData(propsData);
        } else if (id) {
            handlePrint(id);
        }
    }, [id, propsData]);

    useEffect(() => {
        if (data?.items) {
            // Reset rows before adding new ones to avoid duplication
            setRows([]);
            const newRows = data.items.map((value) => ({
                item: {
                    id: value.itemId,
                    name: value.itemName,
                },
                selfEvaluate: value?.selfEvaluate,
                managementEvaluate: value?.managementEvaluate,
            }));
            setRows(newRows);
        }
    }, [data]);

    let columns = [
        {
            title: t("STT"),
            width: "80",
            render: (rowData) => rowData?.tableData?.id + 1,
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
        },
        {
            title: "Đầu mục công việc \n" + "(Liệt kê nội dung công việc đã thực hiện trong thời gian thử việc)",
            minWidth: "200px",
            field: "authority",
            render: (rowData) => <span>{row[rowData?.tableData?.id]?.item?.name || ""}</span>,
        },
        {
            title: "Nhân viên tự đánh giá (Đạt/Không đạt)",
            minWidth: "200px",
            render: (rowData) => (
                <span>
                    {row[rowData?.tableData?.id]?.selfEvaluate === EVALUATION_STATUS.PASS ? "Đạt" : "Không đạt"}
                </span>
            ),
            ...Config.tableCellConfig,
        },
        {
            title: "Quản lý trực tiếp đánh giá (Đạt/Không đạt)",
            minWidth: "200px",
            field: "createdBy",
            render: (rowData) => (
                <span>
                    {row[rowData?.tableData?.id]?.managementEvaluate === EVALUATION_STATUS.PASS ? "Đạt" : "Không đạt"}
                </span>
            ),
            ...Config.tableCellConfig,
        },
    ];
    return (
        <Grid container spacing={2}>
            {/* Logo */}
            <Grid item xs={12}>
                <TableContainer
                    component={Paper}
                    sx={{
                        margin: "auto",
                        border: "2px solid black",
                        boxShadow: 3,
                    }}>
                    <Table>
                        <TableBody>
                            <TableRow>
                                {/* Cột Logo */}
                                <TableCell
                                    sx={{
                                        width: "25%",
                                        textAlign: "center",
                                        border: "2px solid black",
                                        backgroundColor: "#f9f9f9",
                                    }}>
                                    <Box>
                                        <img
                                            src={ConstantList.ROOT_PATH + "assets/images/logo.png"}
                                            style={{
                                                maxWidth: "90px",
                                                display: "block",
                                                margin: "auto",
                                            }}
                                        />
                                    </Box>
                                </TableCell>

                                {/* Cột Tiêu đề */}
                                <TableCell
                                    sx={{
                                        textAlign: "center",
                                        border: "2px solid black",
                                        fontWeight: "bold",
                                        fontSize: "1.2rem",
                                        padding: "20px",
                                    }}>
                                    <Typography variant='h5' fontWeight='bold'>
                                        ĐÁNH GIÁ NHÂN SỰ
                                    </Typography>
                                    <Typography variant='h5' fontWeight='bold'>
                                        TÁI KÍ HỢP ĐỒNG LAO ĐỘNG
                                    </Typography>
                                </TableCell>

                                {/* Cột Thông tin */}
                                <TableCell
                                    sx={{
                                        width: "20%",
                                        border: "2px solid black",
                                        fontSize: "0.9rem",
                                        padding: "5px",
                                    }}>
                                    <Table size='small'>
                                        <TableBody>
                                            <TableRow>
                                                <TableCell sx={{ fontWeight: "bold", padding: "6px" }}>Mã số</TableCell>
                                                <TableCell sx={{ padding: "6px" }}></TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell sx={{ fontWeight: "bold", padding: "6px" }}>
                                                    Lần BH
                                                </TableCell>
                                                <TableCell sx={{ padding: "6px" }}></TableCell>
                                            </TableRow>
                                            <TableRow>
                                                <TableCell sx={{ fontWeight: "bold", padding: "6px" }}>
                                                    Ngày BH
                                                </TableCell>
                                                <TableCell sx={{ padding: "6px" }}></TableCell>
                                            </TableRow>
                                        </TableBody>
                                    </Table>
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
            {/* Thông tin nhân viên */}
            <Grid item xs={12}>
                <TableContainer component={Paper} sx={{ marginTop: 2, border: "1px solid black" }}>
                    <Table size='small'>
                        <TableBody>
                            <TableRow>
                                <TableCell sx={{ width: "25%", fontWeight: "bold" }}>Họ và tên:</TableCell>
                                <TableCell>{data?.staffName || ""}</TableCell>
                                <TableCell sx={{ width: "20%", fontWeight: "bold" }}>Mã nhân viên:</TableCell>
                                <TableCell>{data?.staffCode || ""}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell sx={{ fontWeight: "bold" }}>Chức danh:</TableCell>
                                <TableCell>{data?.position || ""}</TableCell>
                                <TableCell sx={{ fontWeight: "bold" }}>Ban:</TableCell>
                                <TableCell>{data?.department || ""}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell sx={{ fontWeight: "bold" }}>Phòng/Cơ sở:</TableCell>
                                <TableCell>{data?.division || ""}</TableCell>
                                <TableCell sx={{ fontWeight: "bold" }}>Bộ phận/Nhóm:</TableCell>
                                <TableCell>{data?.team || ""}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell sx={{ fontWeight: "bold" }}>Quản lý trực tiếp:</TableCell>
                                <TableCell>{data?.directManagerName || ""}</TableCell>
                                <TableCell sx={{ fontWeight: "bold" }}>Ngày nhận việc:</TableCell>
                                <TableCell>{getDate(data?.hireDate) || ""}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell sx={{ fontWeight: "bold" }}>Thời hạn HĐLĐ trước:</TableCell>
                                <TableCell colSpan={3}>{getDate(data?.previousContractDuration) || ""}</TableCell>
                            </TableRow>
                            <TableRow>
                                <TableCell sx={{ fontWeight: "bold" }}>Loại HĐLĐ:</TableCell>
                                <TableCell colSpan={3}>
                                    <FormGroup row>
                                        {listContractType?.map((contract) => {
                                            if (contract.code === "XĐTH" || contract.code === "KXĐTH") {
                                                return (
                                                    <FormControlLabel
                                                        key={contract.id}
                                                        control={
                                                            <Checkbox
                                                                name='contractTypeName'
                                                                checked={data?.contractTypeId === contract?.id}
                                                            />
                                                        }
                                                        label={contract.name}
                                                        sx={{ marginRight: 4 }}
                                                    />
                                                );
                                            }
                                        })}
                                    </FormGroup>
                                </TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
            {/* Phần A */}
            <Grid item xs={12}>
                <Typography style={{ fontWeight: "700", fontSize: "large", color: "red" }}>
                    A. NỘI DUNG ĐÁNH GIÁ:
                </Typography>
            </Grid>
            <Grid item xs={12} md={12}>
                <Typography style={{ fontWeight: "700", fontSize: "small", color: "blue" }}>
                    1. Kết quả thực hiện công việc:
                </Typography>
            </Grid>
            <Grid item xs={12} md={12}>
                <GlobitsTable columns={columns} data={row} nonePagination />
            </Grid>
            <Grid item xs={12}>
                <Typography style={{ fontWeight: "700", fontSize: "small", color: "blue" }}>
                    2. Nhận xét khác của Quản lý trực tiếp:
                </Typography>
                <Box sx={{ padding: "16px", marginTop: 1 }}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>- Ưu điểm:</Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.advantage || ""}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>- Nhược điểm:</Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.disadvantage || ""}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                    - Chấp hành nội quy, quy định của công ty:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.companyPolicyCompliance || ""}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                    - Mối quan hệ với đồng nghiệp:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.coworkerRelationship || ""}
                                </Typography>
                            </Box>
                        </Grid>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                    - Tinh thần trách nhiệm:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.senseOfResponsibility || ""}
                                </Typography>
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Grid>
            {/* Phần B */}
            <Grid item xs={12} md={12}>
                <Typography style={{ fontWeight: "700", fontSize: "large", color: "red" }}>B. KẾT LUẬN:</Typography>
                <Box sx={{ padding: "16px", marginTop: 1 }}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography
                                    sx={{
                                        fontWeight: "bold",
                                        minWidth: "300px",
                                        display: "flex",
                                        alignItems: "center",
                                    }}>
                                    <Checkbox
                                        checked={data?.contractRecommendation === true}
                                        disabled
                                        size='small'
                                        sx={{ marginRight: 1 }}
                                    />
                                    Đạt yêu cầu, đề xuất ký HĐLĐ kể từ ngày:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                        fontWeight: "bold",
                                    }}>
                                    {data?.contractRecommendation === true
                                        ? getDate(data?.contractRecommendationDateFrom)
                                        : ""}
                                </Typography>
                                <Typography sx={{ padding: "0 32px" }}> đến ngày </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                        fontWeight: "bold",
                                    }}>
                                    {data?.contractRecommendation === true
                                        ? getDate(data?.contractRecommendationDateTo)
                                        : ""}
                                </Typography>
                            </Box>
                            {data?.contractRecommendation === true && (
                                <>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            - Chức danh:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.positionTitleName || ""}
                                        </Typography>
                                    </Box>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            - Cấp bậc:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.rankTitleName || ""}
                                        </Typography>
                                    </Box>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            - Lương cứng:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.baseSalary || ""} VND
                                        </Typography>
                                    </Box>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            - Phụ cấp:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.allowanceAmount || ""} VND
                                        </Typography>
                                    </Box>
                                </>
                            )}
                        </Grid>

                        {/* Đạt yêu cầu */}
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography
                                    sx={{
                                        fontWeight: "bold",
                                        minWidth: "300px",
                                        display: "flex",
                                        alignItems: "center",
                                    }}>
                                    <Checkbox
                                        checked={data?.contractRecommendation === true}
                                        disabled
                                        size='small'
                                        sx={{ marginRight: 1 }}
                                    />
                                    Đạt yêu cầu, đề xuất ký HĐLĐ:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}>
                                    {data?.contractRecommendation === true
                                        ? `Từ ngày ${getDate(data?.contractRecommendationDateFrom)} đến ngày ${getDate(
                                              data?.contractRecommendationDateTo
                                          )}`
                                        : ""}
                                </Typography>
                            </Box>
                            {data?.contractRecommendation === true && (
                                <>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Chức danh:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.positionTitleName || ""}
                                        </Typography>
                                    </Box>

                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Cấp bậc:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.rankTitleName || ""}
                                        </Typography>
                                    </Box>

                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Lương cứng:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.baseSalary?.toLocaleString("vi-VN")} VND
                                        </Typography>
                                    </Box>

                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Phụ cấp:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.allowanceAmount?.toLocaleString("vi-VN")} VND
                                        </Typography>
                                    </Box>

                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Thời gian áp dụng từ ngày:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {getDate(data?.effectiveFromDate)}
                                        </Typography>
                                    </Box>
                                </>
                            )}
                        </Grid>

                        {/* Không đạt yêu cầu */}
                        <Grid item xs={12}>
                            <Box display='flex' alignItems='baseline'>
                                <Typography
                                    sx={{
                                        fontWeight: "bold",
                                        minWidth: "300px",
                                        display: "flex",
                                        alignItems: "center",
                                    }}>
                                    <Checkbox
                                        checked={data?.contractRecommendation === false}
                                        disabled
                                        size='small'
                                        sx={{ marginRight: 1 }}
                                    />
                                    Không đạt yêu cầu:
                                </Typography>
                                <Typography
                                    sx={{
                                        flex: 1,
                                        borderBottom: "1px dotted black",
                                        minHeight: "24px",
                                        paddingLeft: "8px",
                                    }}></Typography>
                            </Box>
                            {data?.contractRecommendation === false && (
                                <>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Ngừng hợp tác kể từ ngày:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.cooperationStatus === true
                                                ? getDate(data?.collaborationEndDate)
                                                : ""}
                                        </Typography>
                                    </Box>
                                    <Box display='flex' alignItems='baseline' sx={{ mt: 2, ml: 4 }}>
                                        <Typography sx={{ fontWeight: "bold", minWidth: "300px" }}>
                                            • Bố trí sang vị trí khác:
                                        </Typography>
                                        <Typography
                                            sx={{
                                                flex: 1,
                                                borderBottom: "1px dotted black",
                                                minHeight: "24px",
                                                paddingLeft: "8px",
                                            }}>
                                            {data?.cooperationStatus === false
                                                ? `${data?.newPositionName} từ ngày ${getDate(
                                                      data?.newPositionTransferDate
                                                  )}`
                                                : ""}
                                        </Typography>
                                    </Box>
                                </>
                            )}
                        </Grid>
                    </Grid>
                </Box>
            </Grid>
        </Grid>
    );
}

export default memo(observer(forwardRef(EvaluationTicketView)));
