import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useFormikContext } from "formik";

function SubOrgInOrgTable() {
    const { t } = useTranslation();

    const { values } = useFormikContext();

    const columns = [
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên công ty con",
            field: "name",
            align: "left",
        },
        {
            title: "Website",
            field: "website",
        },
    ];

    return (
        <GlobitsTable
            data={values?.subDepartments || []}
            columns={columns}
            nonePagination={true}
        />
    );
}

export default memo(observer(SubOrgInOrgTable));
