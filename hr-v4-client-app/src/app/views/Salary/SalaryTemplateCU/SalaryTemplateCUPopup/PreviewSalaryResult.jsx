import React, { useState, useEffect, memo, useMemo } from "react";
import { Formik, Form, Field, useFormikContext } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles, Tooltip } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import Skeleton from '@material-ui/lab/Skeleton';

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "2px",
        overflowX: "auto",
        // overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        // width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));

function PreviewSalaryResult(props) {
    const classes = useStyles();
    const { salaryResultStore } = useStore();

    const {
        isOpenPreview,
        handleClosePreview
    } = salaryResultStore;

    const { t } = useTranslation();

    const { values: data } = useFormikContext();

    const columnHeaders = useMemo(function () {
        const columnGroups = [];
        const remainItems = [];

        data?.templateItems?.forEach(function (item) {
            if (item.templateItemGroupId == null) {
                // this is a common column has rowspan = 2 
                let columnItem = JSON.parse(JSON.stringify(item));
                columnItem.isItem = true;

                columnGroups.push(columnItem);
            }
            else {
                // these code below handle for column group and its item

                // 1st case: the group existed right before => merge column with old consecutive before
                if (columnGroups.length > 0 && columnGroups[columnGroups.length - 1].id == item.templateItemGroupId) {
                    columnGroups[columnGroups.length - 1].colSpan++;
                }
                // 2nd case: the group is not appear right before => add new group
                else {
                    let group = data?.templateItemGroups.find(function (group) {
                        return group.id == item.templateItemGroupId;
                    });
                    group = JSON.parse(JSON.stringify(group));
                    group.colSpan = 1;

                    columnGroups.push(group);
                }

                remainItems.push(JSON.parse(JSON.stringify(item)));
            }
        });

        return {
            columnGroups,
            remainItems
        };
    }, [data?.templateItemGroups, data?.templateItems]);

    const loopTimes = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="xl"
            open={isOpenPreview}
            noDialogContent
            title={"Xem trước cấu trúc bảng lương"}
            onClosePopup={handleClosePreview}
        >
            <div className="dialog-body">
                <DialogContent className="p-12">
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <section className={classes.tableContainer}>
                                <table className={`${classes.table} w-100`} style={{ tableLayout: "auto" }}>
                                    <thead>
                                        <tr className={classes.tableHeader}>
                                            {columnHeaders.columnGroups.map(function (column, index) {

                                                return (
                                                    <React.Fragment key={index}>
                                                        {column?.isItem && (
                                                            <th rowSpan={2} align="center" style={{ minWidth: "120px" }}>
                                                                {column?.displayName}
                                                            </th>
                                                        )}

                                                        {!column?.isItem && (
                                                            <th colSpan={column?.colSpan} align="center">
                                                                {column?.name}
                                                            </th>
                                                        )}
                                                    </React.Fragment>
                                                );
                                            })}
                                        </tr>

                                        <tr className={classes.tableHeader}>
                                            {columnHeaders.remainItems.map(function (column, index) {

                                                return (
                                                    <th key={index} align="center" style={{ minWidth: "120px" }}>
                                                        {column?.displayName}
                                                    </th>
                                                );
                                            })}

                                        </tr>
                                    </thead>

                                    <tbody>
                                        {
                                            loopTimes?.map(function (times, index) {
                                                return (
                                                    <tr key={index} className='row-table-body row-table-no_data'>
                                                        {
                                                            data?.templateItems?.map(function (item, jndex) {
                                                                return (
                                                                    <td key={index + "_" + jndex} className="px-6">
                                                                        {/* <Skeleton
                                                                            animation="wave"
                                                                            height={30}
                                                                        /> */}
                                                                        <div style={{ height: "30px" }}></div>
                                                                    </td>
                                                                );
                                                            })
                                                        }
                                                    </tr>
                                                );
                                            })
                                        }
                                    </tbody>
                                </table>


                            </section>
                        </Grid>
                    </Grid>

                </DialogContent>
            </div>

            {/* <div className="dialog-footer dialog-footer-v2 py-8">
                <DialogActions className="p-0">
                    <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon />}
                            variant="contained"
                            className="mr-12 btn btn-secondary d-inline-flex"
                            color="secondary"
                            onClick={handleClosePreview}
                        >
                            {t("general.button.cancel")}
                        </Button>
                    </div>
                </DialogActions>
            </div> */}
        </GlobitsPopupV2 >
    );
}

export default memo(observer(PreviewSalaryResult));