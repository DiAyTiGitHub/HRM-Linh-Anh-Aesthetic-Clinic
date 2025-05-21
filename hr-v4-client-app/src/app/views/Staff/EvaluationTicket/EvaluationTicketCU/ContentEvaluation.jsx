import {useTranslation} from "react-i18next";
import {Button, ButtonGroup, Grid, Icon, IconButton, Tooltip, Typography} from "@material-ui/core";
import GlobitsPagingAutocomplete from "../../../../common/form/GlobitsPagingAutocomplete";
import {pagingEvaluationItems} from "../../../System/SystemParam/Evaluation/EvaluationItem/EvaluationItemService";
import GlobitsSelectInput from "../../../../common/form/GlobitsSelectInput";
import {EVALUATE_PERSON, EVALUATION_STATUS} from "../../../../LocalConstants";
import Config from "../../../../common/GlobitsConfigConst";
import AddIcon from "@material-ui/icons/Add";
import GlobitsTable from "../../../../common/GlobitsTable";
import React from "react";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";

const ContentEvaluation = ({row, setRows, evaluatePerson}) => {
    const {t} = useTranslation();
    const handleInputChange = (index, value, props) => {
        setRows(prevRows => {
            const updatedRows = [...prevRows];  // Tạo một bản sao của rows
            updatedRows[index][props] = value; // Cập nhật giá trị `selected` của dòng
            return updatedRows;  // Trả về state mới
        });
        console.log(row)
    };
    const handleRemoveRow = (indexToRemove) => {
        setRows(prevRows => prevRows.filter((_, index) => index !== indexToRemove));
    };

    let columns = [
        {
            title: t("STT"),
            width: "80",
            render: (rowData) => rowData?.tableData?.id + 1,
            cellStyle: {textAlign: "center"},
            headerStyle: {textAlign: "center"},
        },
        (evaluatePerson !== EVALUATE_PERSON.STAFF ? {
            title: t("general.action"),
            cellStyle: {textAlign: "center"},
            render: (rowData) => (
                <IconButton size='small' onClick={() => handleRemoveRow(rowData?.tableData?.id)}>
                    <Icon fontSize='small' color='error'>
                        delete
                    </Icon>
                </IconButton>
            ),
        } : {}),
        {
            title: "Đầu mục công việc \n" + "(Liệt kê nội dung công việc đã thực hiện trong thời gian thử việc)",
            minWidth:
                "200px",
            field:
                "authority",
            render:
                (rowData) => (
                    <>
                        <GlobitsPagingAutocomplete
                            name={rowData?.tableData?.id}
                            customData={'data'}
                            displayData='name'
                            api={pagingEvaluationItems}
                            value={{
                                id: row[rowData?.tableData?.id]?.item?.id,
                                name: row[rowData?.tableData?.id]?.item?.name
                            }}
                            readOnly={evaluatePerson === EVALUATE_PERSON.STAFF}
                            // Truyền giá trị đã chọn từ state
                            onChange={(__, value) => handleInputChange(rowData?.tableData?.id, value, 'item')}
                        />
                    </>
                ),
        },
        {
            title: "Nhân viên tự đánh giá (Đạt/Không đạt)",
            minWidth:
                "200px",
            render:
                (rowData) => (
                    <GlobitsSelectInput
                        name="selfEvaluate"
                        keyValue="id"
                        options={[
                            {id: EVALUATION_STATUS.PASS, name: "Đạt"},
                            {id: EVALUATION_STATUS.FAIL, name: "Không đạt"}
                        ]}
                        value={row[rowData?.tableData?.id]?.selfEvaluate}
                        handleChange={(event) => handleInputChange(rowData?.tableData?.id, event.target.value, 'selfEvaluate')}
                    />
                ),
            ...
                Config.tableCellConfig,
        },
        (evaluatePerson !== EVALUATE_PERSON.STAFF ? {
            title: "Quản lý trực tiếp đánh giá (Đạt/Không đạt) ",
            minWidth:
                "200px",
            field:
                "createdBy",
            render:
                (rowData) => (
                    <GlobitsSelectInput
                        name="managementEvaluate"
                        keyValue="id"
                        options={[
                            {id: EVALUATION_STATUS.PASS, name: "Đạt"},
                            {id: EVALUATION_STATUS.FAIL, name: "Không đạt"}
                        ]}
                        value={row[rowData?.tableData?.id]?.managementEvaluate}
                        handleChange={(event) => handleInputChange(rowData?.tableData?.id, event.target.value, 'managementEvaluate')}
                    />
                ),
            ...
                Config.tableCellConfig,
        } : {}),
    ];
    const addItemTable = () => {
        const newRow = {id: row.length + 1, selected: null}; // Example row data
        setRows([...row, newRow]); // Add the new row to the existing rows
    };
    return (
        <>
            <Grid item xs={12} md={12}>
                <Typography style={{fontWeight: '700', fontSize: "large", color: 'red'}}>
                    A. NỘI DUNG ĐÁNH GIÁ:
                </Typography>
            </Grid>
            <Grid item xs={12} md={12}>
                <Typography style={{fontWeight: '700', fontSize: "small", color: 'blue'}}>
                    1. Kết quả thực hiện công việc:
                </Typography>
            </Grid>
            {evaluatePerson !== EVALUATE_PERSON.STAFF && (
                <ButtonGroup>
                    <Tooltip placement='top' title='Thêm mới phiếu đánh giá'>
                        <Button
                            startIcon={<AddIcon fontSize='small'/>}
                            onClick={() => addItemTable()}>
                            {t("general.button.add")}
                        </Button>
                    </Tooltip>
                </ButtonGroup>
            )}
            <GlobitsTable
                columns={columns}
                data={row}
                nonePagination
            />
            <Grid container style={{display: 'flex', gap: 12}}>
                <Grid item xs={12} md={12}>
                    <Typography style={{fontWeight: '700', fontSize: "small", color: 'blue'}}>
                        2. Nhận xét khác của Quản lý trực tiếp:
                    </Typography>
                    <Grid spacing={2} container>
                        <Grid item xs={12} md={6}>
                            <GlobitsTextField multiline label={t("Ưu điểm")}
                                              disabled={evaluatePerson === EVALUATE_PERSON.STAFF} name='advantage'/>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <GlobitsTextField disabled={evaluatePerson === EVALUATE_PERSON.STAFF} multiline
                                              label={t("Nhược điểm")}
                                              name='disadvantage'/>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <GlobitsTextField disabled={evaluatePerson === EVALUATE_PERSON.STAFF} multiline
                                              label={t("Chấp hành nội quy, quy định của công ty")}
                                              name='companyPolicyCompliance'/>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <GlobitsTextField disabled={evaluatePerson === EVALUATE_PERSON.STAFF} multiline
                                              label={t("Mối quan hệ với đồng nghiệp")}
                                              name='coworkerRelationship'/>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <GlobitsTextField disabled={evaluatePerson === EVALUATE_PERSON.STAFF} multiline
                                              label={t("Tinh thần trách nhiệm")}
                                              name='senseOfResponsibility'/>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
        </>
    )
}
export default ContentEvaluation