import React from "react";
import { Skeleton } from "@material-ui/lab";

const SkeletonTable = ({ rows = 5, columns = 4 }) => {
    return (
        <div style={{ width: "100%", overflowX: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                    <tr>
                        {Array.from({ length: columns }).map((_, index) => (
                            <th key={index} style={{ padding: "8px", textAlign: "left" }}>
                                <Skeleton variant="text" width="100%" />
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {Array.from({ length: rows }).map((_, rowIndex) => (
                        <tr key={rowIndex}>
                            {Array.from({ length: columns }).map((_, colIndex) => (
                                <td key={colIndex} style={{ padding: "8px" }}>
                                    <Skeleton variant="rect" width="100%" height={20} />
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default SkeletonTable;
