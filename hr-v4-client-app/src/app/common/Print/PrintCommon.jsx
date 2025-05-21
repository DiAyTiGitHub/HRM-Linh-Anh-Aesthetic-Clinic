import React, { forwardRef } from 'react';

const PrintCommon = forwardRef(({ children, marginPage = '8mm', size = 'A4', orientation = 'portrait' }, ref) => {
    const pageId = `${size}-${orientation}`;

    return (
        <div hidden>
            <section className="modal-body-patient" ref={ref} id={pageId}>
                <style
                    dangerouslySetInnerHTML={{
                        __html: `
                        @page ${size}-portrait {
                            size: ${size} portrait !important;
                            margin: ${marginPage} !important;
                        }

                        @page ${size}-landscape {
                            size: ${size} landscape !important;
                            margin: ${marginPage} !important;
                        }

                        
                         
                        @media print {
                        #${pageId} {
                            page: ${pageId};
                        }
                        html, body {
                            height: initial !important;
                            overflow: initial !important;
                            -webkit-print-color-adjust: exact;
                            font: 13px 'Times New Roman';
                            font-family: 'Times New Roman', Times, serif; /* Đặt phông chữ mặc định */
                        }
                        .highcharts-container {
                            width: 100% !important;
                            height: auto !important;
                        }
                        .hidden-on-print {
                            display: none;
                        }
                        .page-break {
                            margin-top: 1rem;
                            height: 0;
                            page-break-before: auto;
                        }
                        .labtest-recent-container {
                            display: flex !important;
                            justify-content: space-around;
                            margin-top: 8px;
                            margin-bottom: 8px;
                        }
                        .labtest-table {
                            width: 100%;
                            border-collapse: collapse;
                            font-family: 'Times New Roman', Times, serif; /* Đặt phông chữ mặc định */
                        }
                        .col-header {
                            vertical-align: middle;
                            text-align: center;
                            background: #d9d9d9;
                            border: 1px solid #000;
                            font: bold 13px 'Times New Roman';
                            color: #0d0d0d;
                            line-height: 16px;
                            height: 24px;
                        }
                        .col-labtest-name {
                            width: 28%;
                            white-space: nowrap;
                        }
                        .col-labtest-field {
                            width: 18%;
                        }
                        .col-body {
                            font: 13px 'Times New Roman';
                            color: #0d0d0d;
                            line-height: 16px;
                            height: 28px;
                            border: 1px solid #000;
                            padding: 0 4px;
                        }
                            th, td {
          font-family: 'Times New Roman', Times, serif;
        }
                        .col-text-center {
                            text-align: center;
                        }
                        ${Array.from({ length: 13 })
                            .map(
                                (_, index) => `
                            .col-${index} {
                                width: calc(100% / 12 * ${index});
                            }
                            `
                            )
                            .join("")}
                        ${Array.from({ length: 30 })
                            .map(
                                (_, index) => `
                            .pl-${index} {
                                padding-left: ${index}pt;
                            }
                            .mt-${index} {
                                margin-top: ${index}pt;
                            }
                            .mb-${index} {
                                margin-bottom: ${index}pt;
                            }
                            .mx-${index} {
                                margin-left: ${index}pt;
                                margin-right: ${index}pt;
                            }
                            .my-${index} {
                                margin-top: ${index}pt;
                                margin-bottom: ${index}pt;
                            }
                            .col-gap-${index} {
                                column-gap: ${index}pt;
                            }
                            .gap-${index} {
                                gap: ${index}pt;
                            }
                            `
                            )
                            .join("")}
                        .table-collapse {
                            border-collapse: collapse;
                            border: 1px solid #000;
                        }
                        .table-collapse th,
                        .table-collapse td,
                        .table-collapse tr {
                            color: black;
                            padding: 2px;
                            border: 1px solid #000;
                        }
                        .table-collapse td {
                            min-height: 25px !important;
                        }
                        table {
                            width: 100%;
                            font-family: 'Times New Roman', Times, serif;
                        }
                        th {
                            font-weight: bold;
                        }
                        .text-right {
                            text-align: right;
                        }
                        .table-layout-fixed {
                            table-layout: fixed;
                        }
                        .text-center {
                            text-align: center;
                        }
                        .justify-center {
                            justify-content: center;
                        }
                        .justify-end {
                            justify-content: end;
                        }
                        .bg-gray {
                            background-color: #ccc;
                        }
                        .borderTop-gray {
                            border-top: 1px solid #ccc;
                        }
                        .flex {
                            display: flex;
                        }
                        .flex-warp {
                            flex-wrap: wrap;
                        }
                        .border-box {
                            display: inline-flex;
                            border: 2px solid #000;
                            // padding: 2px;
                            text-align: center;
                            align-items: center;
                            justify-content: center;
                            min-height: 20px;
                            min-width: 20px;
                            }
                        }`
                    }}
                />


                {children}
            </section>
        </div>
    )
});

export default PrintCommon;