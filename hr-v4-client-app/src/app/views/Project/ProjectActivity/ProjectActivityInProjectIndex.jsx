import React, {memo, useEffect, useState} from "react";
import {useParams} from "react-router";
import {observer} from "mobx-react";
import DescriptionIcon from "@material-ui/icons/Description";
import {Grid, IconButton, Tooltip} from "@material-ui/core";
import moment from "moment";
import {useStore} from "../../../stores";
import {useTranslation} from "react-i18next";
import {makeStyles} from "@material-ui/core/styles";
import ActivityCreateEditPopup from "./ActivityCreateEditPopup";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import MaterialTable, {MTableBodyRow, MTableEditRow} from "material-table";
import ProjectToolbar from "./ProjectActivityToolbar";

const useStyles = makeStyles({
    actions: {
        border: "1px solid #000",
    },
    tableRow: {
        "& td,& th": {
            border: "1px solid #000",
        },
        // "& td:last-child": {
        //   width: "10% !important",
        // },
        // "& td:first-child": {
        //   width: "3% !important",
        // },
        "& div": {
            justifyContent: "center",
            "& .MuiIconButton-root": {
                padding: "4px !important",
            },
        },
    },
});

function ProjectActivityInProjectIndex() {
    const {id} = useParams();
    const {t} = useTranslation();
    const {projectStore, projectActivityStore} = useStore();

    const {
        resetStore,
        handleOpenActivityPopup,
        handleDeleteActivity,
        getListActivityById,
        listActivities,
        setProjectId,
        openCreateEditPopup,
        openConfirmDeletePopup,
        handleClosePopup,
        handleConfirmDeleteActivity,

    } = projectActivityStore;

    const {
        dataEditProject,
        getActivityByProject,
        canCreateUpdateDeleteProject,
        handleOpenTaskByActivity,
    } = projectStore;

    const [selectedRow, setSelectedRow] = useState(null);
    const classes = useStyles();

    useEffect(function () {
        setProjectId(id);
        // pagingProjectActivity({ projectId: id, includeAll: true });
        getListActivityById({projectId: id, includeAll: true});
        return resetStore;
    }, []);

    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <ProjectToolbar/>
                </Grid>
                <Grid item xs={12}>
                    <MaterialTable
                        classes={{actions: classes.actions}}
                        title="Danh sách hoạt động"
                        actions={!canCreateUpdateDeleteProject() ? [] : [
                            {
                                icon: "add",
                                tooltip: "Thêm hoạt động",
                                onClick: (event, rowData) =>
                                    getActivityByProject(dataEditProject?.id, rowData?.id).then(
                                        () => handleOpenActivityPopup(rowData, true, id)
                                    ),
                            },
                            (rowData) => ({
                                icon: "edit",
                                tooltip: "Sửa hoạt động",
                                onClick: (event, rowData) => handleOpenActivityPopup(rowData),
                            }),
                            (rowData) => ({
                                icon: "delete",
                                tooltip: "Xóa hoạt động",
                                onClick: (event, rowData) =>
                                    handleDeleteActivity({...rowData, project: {id}}),
                            }),
                        ]}
                        columns={[
                            {
                                field: "code",
                                title: "Mã",
                                cellStyle: {
                                    width: "8%",
                                    textAlign: "center",
                                },
                            },
                            {
                                field: "name",
                                title: "Tên hoạt động",
                                cellStyle: {
                                    textAlign: "left",
                                },
                                render: (rowData) => (
                                    <>
                                        <div className="flex flex-start flex-middle">
                                            <Tooltip placement="top" title="Danh sách phần việc của hoạt động">
                                                <IconButton
                                                    className="px-6"
                                                    aria-label="description"
                                                    onClick={() => handleOpenTaskByActivity(rowData?.id)}
                                                >
                                                    <DescriptionIcon/>
                                                </IconButton>
                                            </Tooltip>
                                            <p className="p-0 m-0">{rowData?.name}</p>
                                        </div>
                                    </>
                                ),
                            },
                            // {
                            //   field: "duration",
                            //   title: "Ước lượng",
                            //   emptyValue: "0",
                            //   cellStyle: {
                            //     textAlign: "center",
                            //   },
                            // },
                            {
                                field: "startTime",
                                title: "Thời gian bắt đầu",
                                emptyValue: " ",
                                cellStyle: {
                                    width: "12%",
                                    textAlign: "center",
                                },
                                render: (rowData) => (
                                    <span className="px-8">
                    {moment(rowData?.startTime).format("DD/MM/YYYY hh:mm A")}{" "}
                  </span>
                                ),
                            },
                            {
                                field: "endTime",
                                title: "Thời gian kết thúc",
                                emptyValue: " ",
                                cellStyle: {
                                    width: "12%",
                                    textAlign: "center",
                                },
                                render: (rowData) => (
                                    <span className="px-8">
                    {moment(rowData?.endTime).format("DD/MM/YYYY hh:mm A")}
                  </span>
                                ),
                            },
                            {
                                field: "description",
                                title: "Mô tả",
                                emptyValue: " ",
                                cellStyle: {
                                    textAlign: "left",
                                    width: "32%",
                                    padding: "2px 8px"
                                },
                                // render: (rowData) => (
                                //   <span>
                                //     {moment(rowData?.endTime).format("DD/MM/YYYY hh:mm A")}
                                //   </span>
                                // ),
                            },
                        ]}
                        data={listActivities}
                        parentChildData={(row, rows) =>
                            rows.find((a) => a?.id === row?.parentId)
                        }
                        onRowClick={(evt, selectedRow) =>
                            setSelectedRow(selectedRow?.tableData?.id)
                        }
                        options={{
                            toolbar: false,
                            showTitle: false,
                            search: false,
                            paging: false,
                            defaultExpanded: true,
                            headerStyle: {
                                backgroundColor: "rgb(246 246 246)",
                                color: "#717276",
                                fontSize: "13px",
                                textAlign: "center",
                                fontWeight: "bold",
                                border: "1px solid #000 ",
                            },
                            actionsColumnIndex: -1,
                            rowStyle: (rowData, index) => ({
                                backgroundColor:
                                    selectedRow === rowData?.tableData?.id ? "#EEE" : "#FFF",
                                fontWeight: rowData?.parent ? "normal" : "bold",
                            }),
                        }}
                        components={{
                            Row: (props) => (
                                <MTableBodyRow {...props} className={classes.tableRow}/>
                            ),
                            EditRow: (props) => (
                                <MTableEditRow {...props} className={classes.tableRow}/>
                            ),
                        }}
                        localization={{
                            body: {
                                emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
                            },
                        }}
                    />
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <ActivityCreateEditPopup dataEditProject={dataEditProject}/>
            )}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClosePopup}
                    onYesClick={() => handleConfirmDeleteActivity(dataEditProject)}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    );
}

export default memo(observer(ProjectActivityInProjectIndex));
