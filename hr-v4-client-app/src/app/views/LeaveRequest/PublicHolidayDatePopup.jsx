import { observer } from 'mobx-react';
import React, { memo, useEffect } from "react";
import { Icon, IconButton, Grid, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';
import { getDate, getDateTime } from 'app/LocalFunction';
import GlobitsPopupV2 from "../../common/GlobitsPopupV2";
import { useFormikContext } from "formik";

function PublicHolidayDatePopup({ open, handleClose }) {
    const { publicHolidayDateStore } = useStore();
    const { t } = useTranslation();
    const { values } = useFormikContext();

    const {
        publicHolidayDateList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        pagingPublicHolidayDate,
        setSearchObject,
    } = publicHolidayDateStore;

    useEffect(() => {
        if (open) {
            const newSearchObject = {
                ...searchObject,
                pageIndex: 0,
                fromDate: values?.fromDate || null,
                toDate: values?.toDate || null,
            };
            setSearchObject(newSearchObject);
            pagingPublicHolidayDate();
        }
    }, [open, values?.fromDate, values?.toDate]);

    const columns = [
        {
            title: t("navigation.publicHolidayDate.holidayDate"),
            field: "holidayDate",
            render: (data) => <span>{getDate(data?.holidayDate)}</span>,
            align: "left",
            width: "40%",
        },
        {
            title: t("navigation.publicHolidayDate.holidayType"),
            field: "holidayType",
            width: "40%",
            render: (data) => (
                <span>
                    {LocalConstants.HolidayLeaveType.getListData().find(
                        (i) => i.value === data?.holidayType
                    )?.name}
                </span>
            ),
            align: "left",
        },
        {
            title: t("navigation.publicHolidayDate.salaryCoefficient"),
            field: "salaryCoefficient",
            render: (data) => <span>{data?.salaryCoefficient}</span>,
            align: "left",
            width: "6%",
        },
    ];

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"publicHolidayDateList"}
            title="Danh sách ngày nghỉ"
            size="md"
            open={open}
            scroll="body"
            onClosePopup={handleClose}
        >
            <Grid container className="p-12">
                <Grid item xs={12}>
                    <GlobitsTable
                        selection={false}
                        data={publicHolidayDateList}
                        columns={columns}
                        totalPages={totalPages}
                        handleChangePage={handleChangePage}
                        setRowsPerPage={setPageSize}
                        pageSize={searchObject?.pageSize}
                        pageSizeOption={[10, 15, 25, 50, 100]}
                        totalElements={totalElements}
                        page={searchObject?.pageIndex}
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(PublicHolidayDatePopup));