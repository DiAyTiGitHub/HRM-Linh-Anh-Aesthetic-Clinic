import React, { useState, useEffect, memo, useMemo } from "react";
import { FieldArray, Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { IconButton, Icon, Tooltip, Grid, makeStyles } from "@material-ui/core";
import SalaryBoardRowContainer from "./SalaryBoardRow/SalaryBoardRowContainer";
import FormikFocusError from "app/common/FormikFocusError";

function SalaryResultBoard() {
    const { t } = useTranslation();

    const { values, setFieldValue } = useFormikContext();
 
    const columnHeaders = useMemo(function () {
        const columnGroups = [];
        const remainItems = []; 

        const data = values;

        data?.resultItems?.forEach(function (item) {
            if (item.resultItemGroupId == null) {
                // this is a common column has rowspan = 2 
                let columnItem = JSON.parse(JSON.stringify(item));
                columnItem.isItem = true;

                columnGroups.push(columnItem);
            }
            else {
                // these code below handle for column group and its item

                // 1st case: the group existed right before => merge column with old consecutive before
                if (columnGroups.length > 0 && columnGroups[columnGroups.length - 1].id == item.resultItemGroupId) {
                    columnGroups[columnGroups.length - 1].colSpan++;
                }
                // 2nd case: the group is not appear right before => add new group
                else {
                    let group = data?.resultItemGroups.find(function (group) {
                        return group.id == item.resultItemGroupId;
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
    }, [
        values?.resultItemGroups,
        values?.resultItems
    ]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name="salaryResultStaffs">
                    {({ insert, remove, push }) => (
                        <section className="commonTableContainer">
                            <table className={`commonTable w-100`}>
                                <thead>
                                    <tr className="tableHeader">
                                        {/* //add column Actions for editting mode */}
                                        <th rowSpan={2} align="center" style={{ width: "80px" }}
                                            className="stickyCell"
                                        >
                                            Thao tác
                                        </th>

                                        {columnHeaders.columnGroups.map(function (column, index) {

                                            return (
                                                <React.Fragment key={index}>
                                                    {column?.isItem && (
                                                        <th rowSpan={2} align="center" style={{ minWidth: "128px" }}>
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

                                    <tr className="tableHeader">
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
                                        values?.salaryResultStaffs?.map(function (resultStaff, index) {
                                            return (
                                                <SalaryBoardRowContainer
                                                    key={resultStaff?.id}
                                                    rowIndex={index}
                                                    data={resultStaff}
                                                />
                                            );
                                        })
                                    }
                                </tbody>

                            </table>

                        </section>
                    )}
                </FieldArray>

            </Grid>
        </Grid>
    );
}

export default memo(observer(SalaryResultBoard));

