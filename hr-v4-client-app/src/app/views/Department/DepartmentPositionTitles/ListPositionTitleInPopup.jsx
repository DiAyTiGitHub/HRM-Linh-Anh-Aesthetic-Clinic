import React, { memo } from "react";
import { Radio } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import GlobitsTable from "app/common/GlobitsTable";

function ListPositionTitleInPopup() {
    const { t } = useTranslation();
    const { positionTitleV2Store } = useStore();
    const {
        listPositionTitle,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        getTitleType,
    } = positionTitleV2Store;

    const { values, setFieldValue } = useFormikContext();

    function isChosen(rowData) {
        const chosenList = values?.positionTitles || [];
        return chosenList.some(item => item?.id === rowData?.id);
    }

    function handleChoose(rowData) {
        const chosenList = values?.positionTitles || [];
        const itemIndex = chosenList.findIndex(item => item?.id === rowData?.id);

        if (itemIndex === -1) {
            // Item is not chosen yet => add it to chosen list
            chosenList.push(rowData);
        } else {
            // Item is already chosen => remove it from chosen list
            chosenList.splice(itemIndex, 1);
        }

        setFieldValue("positionTitles", [...chosenList]);
    }

    const columns = [
        {
            title: t("general.popup.select"),
            align: "center",
            cellStyle: {
                textAlign: "center",
                display: "flex",
                justifyContent: "center"
            },
            render: (rowData) => {
                const chosen = isChosen(rowData);
                return (
                    <Radio
                        className="pr-8"
                        id={`radio${rowData?.id}`}
                        name="radSelected"
                        value={rowData?.id}
                        checked={chosen}
                        onClick={() => handleChoose(rowData)}
                    />
                );
            },
        },
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên chức danh",
            field: "name",
            align: "left",
        },
        {
            title: "Tên viết tắt",
            field: "shortName",
            align: "left",
        },
        {
            title: "Loại",
            field: "type",
            align: "left",
            render: data => {
                const displayType = getTitleType(data?.type);
                return <span>{displayType}</span>;
            }
        },
        {
            title: "Hệ số phụ cấp",
            field: "positionCoefficient",
            align: "left",
        },
        {
            title: "Cấp bậc",
            field: "rankTitle.name",
            align: "left",
        },
    ];

    return (
        <GlobitsTable
            data={listPositionTitle}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(ListPositionTitleInPopup));
