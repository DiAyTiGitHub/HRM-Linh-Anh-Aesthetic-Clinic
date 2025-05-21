import React, { useEffect, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next';
import clsx from 'clsx';
import { getIn } from 'formik';
import Pagination from "@material-ui/lab/Pagination";

const TableComponent = ({
                            data,
                            columns,
                            onRowClick,
                            titleNodata,
                            className,
                            dataPagination,
                            showOrdinalNumbers,
                            finalRow,
                            finalRowDetial,
                            selectedRow = () => false,
                            selectType,
                            style,
                            maxHeight = "600px",
                            minWidth,
                            handleChangeSelectedRows,
                            resetSelectedRows,
                            initStateRow = [],
                            getRowStyle = () => { },
                            isPaginationHeader = false,
                            rowChild,
                            rowEndElement,
                            valueCompare = "id",
                            doubleClick,
                            stylePagination,
                            finalDetailRow,
                            firstDetailRow,
                            chooseRow,
                            objectDisable = null
                        }) => {
    const [selectedRows, setSelectedRows] = useState(initStateRow);
    const totalColumns = !!(selectType)
        ? (showOrdinalNumbers ? columns?.filter(item => !item?.isHide)?.length + 2 : columns?.filter(item => !item?.isHide)?.length + 1)
        : (showOrdinalNumbers ? columns?.filter(item => !item?.isHide)?.length + 1 : columns?.filter(item => !item?.isHide)?.length);
    const align = columns[0]?.align || 'left';
    const { t } = useTranslation();

    const handleSelectAll = (e) => {
        if (e?.target?.checked) {
            let list = data.filter(item => !selectedRows?.find(sl => sl?.[valueCompare] === item?.[valueCompare]))
            setSelectedRows([...(selectedRows || []), ...list]);
        } else {
            let list = selectedRows.filter(item => !data?.find(sl => sl?.[valueCompare] === item?.[valueCompare]))
            setSelectedRows(list);
        }
    };

    const handleSelectRow = (row, isChecked) => {
        if (selectType === 'multi') {
            if (isChecked) {
                setSelectedRows([...selectedRows, row]);
            } else {
                setSelectedRows(selectedRows.filter(selectedRow => selectedRow?.[valueCompare] !== row?.[valueCompare]));
            }
        } else if (selectType === 'single') {
            if (isChecked) {
                setSelectedRows([row]);
            } else {
                setSelectedRows([]);
            }
        }
    };

    useEffect(() => {
        if (initStateRow?.length > 0) {
            setSelectedRows(initStateRow);
        }
    }, [initStateRow])

    const prevSelectedRowsRef = useRef(selectedRows);

    useEffect(() => {
        if (prevSelectedRowsRef.current !== selectedRows) {
            handleChangeSelectedRows(selectedRows);
            prevSelectedRowsRef.current = selectedRows;
        }
    }, [selectedRows]);

    useEffect(() => {
        if (resetSelectedRows && selectedRows !== initStateRow) {
            setSelectedRows(initStateRow);
        }
    }, [resetSelectedRows]);

    if (!titleNodata) titleNodata = t("noData")

    return (
        <div className={`${className}`}>
            {Boolean(isPaginationHeader) && (
                <div className='flex-wrap justify-between'>
                    <div>
                        {selectedRows?.length > 0 &&
                            <div className='flex align-center p-2 gap-1' style={{ fontWeight: "500" }}>
                                <span className='text-blue' style={{ flexWrap: "nowrap" }}> {selectedRows?.length}</span> mục đã được chọn
                            </div>
                        }
                    </div>
                    {Boolean(dataPagination) && <Pagination {...dataPagination} />}
                </div>
            )}

            <div className={`table-root`} style={{ overflow: "auto" }}>
                <div style={{
                    maxHeight: maxHeight ? maxHeight : "auto",
                    minWidth: minWidth ? minWidth : "auto"
                }}>
                    <table className='table-container' style={{ ...style }}>
                        <thead>
                        <tr className="row-table-header">
                            {selectType === "multi" && (
                                <th align="center" width="28px" style={{ boxSizing: "content-box" }}>
                                    <input type="checkbox" onChange={handleSelectAll}
                                           checked={data?.every(item => selectedRows?.find(sl => sl?.[valueCompare] === item?.[valueCompare]))
                                               && data?.length > 0} />
                                </th>
                            )}

                            {selectType === "single" && (
                                <th></th>
                            )}
                            {showOrdinalNumbers && (
                                <th align={align} width="28px" style={{ boxSizing: "content-box" }}>
                                    STT
                                </th>
                            )}
                            {columns.map((item, index) => (
                                !item?.isHide && (
                                    <th key={index} align="center" width={item?.width} style={{ wordWrap: "break-word" }}>
                                        {typeof item?.title === 'function' ? item?.title() : item?.title}
                                    </th>
                                )
                            ))}
                        </tr>
                        </thead>
                        <tbody>
                        {data && data.length > 0 && firstDetailRow && (firstDetailRow)}
                        {console.log()}
                        {data && data.length > 0 ? (
                            <>
                                {data.map((row, index) => (
                                    <tr
                                        key={index}
                                        className={clsx(`row-table-body pointer row-selection ${(chooseRow && chooseRow?.id === row?.id) ? 'row-choose' : ''}`,
                                            (selectedRow(row) || selectedRows.some(selectedRow => selectedRow?.[valueCompare] === row?.[valueCompare])) && "row-selected")}

                                        onDoubleClick={() => {
                                            if (doubleClick) {
                                                doubleClick(row, index)
                                            }
                                        }}
                                    >
                                        {selectType === "multi" && (
                                            <td align="center" style={getRowStyle(row)}
                                                onClick={() => {
                                                    const isSelected = selectedRows.some(item => item?.[valueCompare] === row?.[valueCompare]);
                                                    const disabled = objectDisable ? (selectedRows.some(selectedRow => (selectedRow?.[valueCompare] === row?.[valueCompare] && selectedRow?.[objectDisable?.field] === objectDisable?.value))) : false
                                                    if (!disabled) { // thêm chức năng disable bên dịch vụ nếu đã là dv trả phí
                                                        handleSelectRow(row, !isSelected);
                                                        if (onRowClick) {
                                                            onRowClick(row);
                                                        }
                                                    }
                                                }}>
                                                <input
                                                    type="checkbox" className='pointer'
                                                    checked={selectedRows.some(selectedRow => selectedRow?.[valueCompare] === row?.[valueCompare])}
                                                    onChange={(e) => handleSelectRow(row, e?.target?.checked)}
                                                    disabled={objectDisable ? selectedRows.some(selectedRow => (selectedRow?.[valueCompare] === row?.[valueCompare] && selectedRow?.[objectDisable?.field] === objectDisable?.value)) : false}

                                                />
                                            </td>
                                        )}
                                        {selectType === "single" && (
                                            <td align="center" style={getRowStyle(row)}
                                                onClick={() => {
                                                    const isSelected = selectedRows.some(item => item?.[valueCompare] === row?.[valueCompare]);
                                                    const disabled = objectDisable ? (selectedRows.some(selectedRow => (selectedRow?.[valueCompare] === row?.[valueCompare] && selectedRow?.[objectDisable?.field] === objectDisable?.value))) : false
                                                    if (!disabled) { // thêm chức năng disable bên dịch vụ nếu đã là dv trả phí
                                                        handleSelectRow(row, !isSelected);
                                                        if (onRowClick) {
                                                            onRowClick(row);
                                                        }
                                                    }
                                                }}>
                                                <input type="radio" className='pointer'
                                                       checked={selectedRows?.[0]?.[valueCompare] === row?.[valueCompare]}
                                                       onChange={(e) => handleSelectRow(row, e?.target?.checked)}
                                                />
                                            </td>
                                        )}
                                        {showOrdinalNumbers && (
                                            <td align={align} style={getRowStyle(row)}>{index + 1}</td>
                                        )}
                                        {columns.map((item, number) => {
                                                return (
                                                    item?.isHide ?
                                                        <></> :
                                                        <td key={number} align={item?.align} width={item?.width}
                                                            style={getRowStyle(row)}>
                                                            {item?.render ? item.render(row, index) : getIn(row, item?.field)}
                                                        </td>
                                                )
                                            }
                                        )}
                                    </tr>
                                ))}
                                {rowChild}
                                {finalRow && (
                                    <tr className={'w-100 bg-green-black'}>
                                        <td colSpan={totalColumns || 0} align='right'>
                                            <h5>
                                                <strong>
                                                    {finalRow}
                                                </strong>
                                            </h5>
                                        </td>
                                    </tr>
                                )}
                                {finalDetailRow && (
                                    finalDetailRow
                                )}
                            </>
                        ) : (
                            <tr className={'row-table-body'}>
                                <td colSpan={totalColumns || 0} align='center'>
                                    {titleNodata}
                                </td>
                            </tr>
                        )}


                        {!!rowEndElement && rowEndElement}
                        {data && data.length > 0 && finalRowDetial && (finalRowDetial)}
                        </tbody>
                    </table>
                </div>
            </div>
            <div className={`flex-wrap justify-between align-center ${stylePagination}`}>
                <div className='flex-wrap align-center gap-1'>
                    {selectedRows?.length > 0 && selectType === "multi" &&
                        <div className='flex align-center p-1 gap-1' style={{ fontWeight: "500" }}>
                            <span style={{ flexWrap: "nowrap" }}> {selectedRows?.length}</span> mục đã chọn
                        </div>
                    }
                    {selectedRows?.length > 0 &&
                        <b className='text-danger pointer' onClick={() => setSelectedRows([])}><i>(Bỏ chọn)</i></b>
                    }
                </div>
                {Boolean(dataPagination) && <Pagination {...dataPagination} />}
            </div>
        </div >
    )
}

export default TableComponent;