import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useFormikContext } from "formik";

function UserInOrgTable() {
    const { t } = useTranslation();

    const { values } = useFormikContext();

    const columns = [
        {
            title: "Họ tên",
            field: "user.person.displayName",
            align: "left",
        },
        {
            title: "Tên tài khoản",
            field: "user.username",
            align: "left",
        },
        {
            title: "Giới tính",
            field: "user.person.gender",
            render: user => (
                <span>
                    {user?.person?.gender === "M" ? "Nam" : user?.person?.gender === "F" ? "Nữ" : ""}
                </span>
            )
        },
        {
            title: "Ngày sinh",
            field: "user.person.birthDate",
            render: (user) => (
                <span>
                    {user?.person?.birthDate && (formatDate("DD/MM/YYYY", user?.person?.birthDate))}
                </span>
            ),
        },
        {
            title: "SĐT",
            field: "user.person.phoneNumber",
        },
        {
            title: "Email",
            field: "user.person.email",
        },
    ];

    return (
        <GlobitsTable
            data={values?.users || []}
            columns={columns}
            nonePagination={true}
        />
    );
}

export default memo(observer(UserInOrgTable));