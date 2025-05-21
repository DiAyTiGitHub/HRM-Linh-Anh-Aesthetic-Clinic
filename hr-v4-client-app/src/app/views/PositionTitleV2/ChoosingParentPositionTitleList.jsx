import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import GlobitsPagination from "../../common/GlobitsPagination";
import MaterialTable from "material-table";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { Radio } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { useFormikContext } from "formik";
import GlobitsTable from "app/common/GlobitsTable";

function ChoosingParentPositionTitleList(props) {
    const { handleClose } = props;
    const { positionTitleV2Store } = useStore();
    const { t } = useTranslation();

    const {
        totalParentElements,
        totalParentPages,
        listParentPositionTitle,
        getTitleType,
        handleParentChangePage,
        searchParentObject,
        setParentPageSize
    } = positionTitleV2Store;

    const { setFieldValue, values } = useFormikContext();

    function handleSelectItem(_, parent) {
        if (values?.parent && values?.parent?.id === parent?.id) {
            setFieldValue("parent", null);
        } else {
            setFieldValue("parent", parent);
            handleClose();
        }
    };

    const columns = [
        {
            width: "8%",
            align: "center",
            title: t("general.popup.select"),
            render: (rowData) => (
                <Radio
                    disabled={values?.id == rowData?.id}
                    id={`radio${rowData?.id}`}
                    name="parent"
                    value={rowData?.id}
                    checked={values?.parent?.id === rowData?.id}
                    onClick={(event) => handleSelectItem(event, rowData)}
                />
            ),
        },
        {
            width: "18%",
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên nhóm ngạch",
            field: "name",
            align: "left",
        },
        {
            title: "Tên viết tắt",
            field: "shortName",
            align: "left",
        },
        // {
        //     title: "Loại",
        //     field: "type",
        //     align: "left",
        //     render: data => {
        //         const displayType = getTitleType(data?.type);

        //         return (
        //             <span>{displayType}</span>
        //         );
        //     }
        // },
    ];

    return (
        <div className="w-100 ">
            <GlobitsTable
                data={listParentPositionTitle}
                columns={columns}
                totalPages={totalParentPages}
                handleChangePage={handleParentChangePage}
                setRowsPerPage={setParentPageSize}
                pageSize={searchParentObject?.pageSize}
                pageSizeOption={[10, 15, 25, 50, 100]}
                totalElements={totalParentElements}
                page={searchParentObject?.pageIndex}
            />
        </div>
    );
}

export default memo(observer(ChoosingParentPositionTitleList));
