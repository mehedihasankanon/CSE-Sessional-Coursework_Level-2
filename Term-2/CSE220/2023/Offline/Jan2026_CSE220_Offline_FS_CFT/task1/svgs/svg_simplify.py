#!/usr/bin/env python3

# python svg_converter.py svgs/excali1.svg svgs/excali1_simple.svg

import re
import sys
import math
import xml.etree.ElementTree as ET


# SVG path tokenizer
TOKEN_RE = re.compile(
    r"""
    [AaCcHhLlMmQqSsTtVvZz]
    |
    [-+]?(?:\d+\.\d*|\.\d+|\d+)(?:[eE][-+]?\d+)?
    """,
    re.VERBOSE,
)


PARAMS = {
    "M": 2,
    "L": 2,
    "H": 1,
    "V": 1,
    "C": 6,
    "S": 4,
    "Q": 4,
    "T": 2,
    "Z": 0,
}


def tokenize(d):
    return TOKEN_RE.findall(d)


def is_command(token):
    return token in PARAMS


def point(x, y):
    return (float(x), float(y))


def add(a, b):
    return (a[0] + b[0], a[1] + b[1])


def sub(a, b):
    return (a[0] - b[0], a[1] - b[1])


def mul(a, s):
    return (a[0] * s, a[1] * s)


def lerp(a, b, t):
    return (
        a[0] + (b[0] - a[0]) * t,
        a[1] + (b[1] - a[1]) * t,
    )


def quadratic_bezier(p0, p1, p2, t):
    a = lerp(p0, p1, t)
    b = lerp(p1, p2, t)
    return lerp(a, b, t)


def cubic_bezier(p0, p1, p2, p3, t):
    a = lerp(p0, p1, t)
    b = lerp(p1, p2, t)
    c = lerp(p2, p3, t)

    d = lerp(a, b, t)
    e = lerp(b, c, t)

    return lerp(d, e, t)


def parse_path(d, curve_steps=40):
    tokens = tokenize(d)

    i = 0
    command = None

    current = (0.0, 0.0)
    start = None

    last_cubic_control = None
    last_quad_control = None

    output = []

    while i < len(tokens):

        # If a command appears, consume it.
        if is_command(tokens[i]):
            command = tokens[i]
            i += 1

        if command is None:
            raise ValueError(f"Path begins with a number: {tokens[i]}")

        upper = command.upper()
        relative = command.islower()

        # Z is special and has no parameters.
        if upper == "Z":
            if start is not None:
                output.append(start)
                current = start

            command = None
            last_cubic_control = None
            last_quad_control = None
            continue

        n = PARAMS[upper]

        # SVG allows multiple parameter sets after one command:
        #
        # M 10 20 30 40 50 60
        #
        # means:
        #
        # M 10 20
        # L 30 40
        # L 50 60

        first = True

        while i < len(tokens) and not is_command(tokens[i]):

            if i + n > len(tokens):
                raise ValueError(
                    f"Not enough parameters for command {command}"
                )

            values = list(map(float, tokens[i:i + n]))
            i += n

            # ---------------------------------------------------------
            # MOVETO
            # ---------------------------------------------------------
            if upper == "M":
                x, y = values

                if relative:
                    x += current[0]
                    y += current[1]

                current = point(x, y)

                if first:
                    output.append(current)
                    start = current

                    # Subsequent coordinate pairs after M are implicit L.
                    first = False
                else:
                    output.append(current)

                last_cubic_control = None
                last_quad_control = None

            # ---------------------------------------------------------
            # LINETO
            # ---------------------------------------------------------
            elif upper == "L":
                x, y = values

                if relative:
                    x += current[0]
                    y += current[1]

                current = point(x, y)
                output.append(current)

                last_cubic_control = None
                last_quad_control = None

            # ---------------------------------------------------------
            # HORIZONTAL LINETO
            # ---------------------------------------------------------
            elif upper == "H":
                x = values[0]

                if relative:
                    x += current[0]

                current = point(x, current[1])
                output.append(current)

                last_cubic_control = None
                last_quad_control = None

            # ---------------------------------------------------------
            # VERTICAL LINETO
            # ---------------------------------------------------------
            elif upper == "V":
                y = values[0]

                if relative:
                    y += current[1]

                current = point(current[0], y)
                output.append(current)

                last_cubic_control = None
                last_quad_control = None

            # ---------------------------------------------------------
            # CUBIC BEZIER
            # ---------------------------------------------------------
            elif upper == "C":
                x1, y1, x2, y2, x, y = values

                if relative:
                    x1 += current[0]
                    y1 += current[1]
                    x2 += current[0]
                    y2 += current[1]
                    x += current[0]
                    y += current[1]

                p0 = current
                p1 = point(x1, y1)
                p2 = point(x2, y2)
                p3 = point(x, y)

                for k in range(1, curve_steps + 1):
                    t = k / curve_steps
                    output.append(
                        cubic_bezier(p0, p1, p2, p3, t)
                    )

                current = p3
                last_cubic_control = p2
                last_quad_control = None

            # ---------------------------------------------------------
            # SMOOTH CUBIC BEZIER
            # ---------------------------------------------------------
            elif upper == "S":
                x2, y2, x, y = values

                if relative:
                    x2 += current[0]
                    y2 += current[1]
                    x += current[0]
                    y += current[1]

                if last_cubic_control is not None:
                    p1 = sub(
                        mul(current, 2),
                        last_cubic_control
                    )
                else:
                    p1 = current

                p0 = current
                p2 = point(x2, y2)
                p3 = point(x, y)

                for k in range(1, curve_steps + 1):
                    t = k / curve_steps
                    output.append(
                        cubic_bezier(p0, p1, p2, p3, t)
                    )

                current = p3
                last_cubic_control = p2
                last_quad_control = None

            # ---------------------------------------------------------
            # QUADRATIC BEZIER
            # ---------------------------------------------------------
            elif upper == "Q":
                x1, y1, x, y = values

                if relative:
                    x1 += current[0]
                    y1 += current[1]
                    x += current[0]
                    y += current[1]

                p0 = current
                p1 = point(x1, y1)
                p2 = point(x, y)

                for k in range(1, curve_steps + 1):
                    t = k / curve_steps
                    output.append(
                        quadratic_bezier(p0, p1, p2, t)
                    )

                current = p2
                last_quad_control = p1
                last_cubic_control = None

            # ---------------------------------------------------------
            # SMOOTH QUADRATIC BEZIER
            # ---------------------------------------------------------
            elif upper == "T":
                x, y = values

                if relative:
                    x += current[0]
                    y += current[1]

                if last_quad_control is not None:
                    p1 = sub(
                        mul(current, 2),
                        last_quad_control
                    )
                else:
                    p1 = current

                p0 = current
                p2 = point(x, y)

                for k in range(1, curve_steps + 1):
                    t = k / curve_steps
                    output.append(
                        quadratic_bezier(p0, p1, p2, t)
                    )

                current = p2
                last_quad_control = p1
                last_cubic_control = None

            else:
                raise ValueError(f"Unsupported command: {command}")

            # M behaves as L for subsequent coordinate pairs.
            if upper == "M":
                upper = "L"
                command = "l" if relative else "L"
                n = 2

    return output


def find_path(filename):
    tree = ET.parse(filename)
    root = tree.getroot()

    # Handle SVG namespaces.
    for element in root.iter():
        if element.tag.endswith("path"):
            d = element.get("d")
            if d:
                return d

    raise ValueError("No <path d=\"...\"> found in SVG")


def fmt(x):
    # Keep the generated SVG reasonably compact.
    if abs(x) < 1e-12:
        x = 0.0

    return f"{x:.8f}".rstrip("0").rstrip(".")


def write_svg(points, filename):
    if not points:
        raise ValueError("No points generated")

    parts = [
        f"M {fmt(points[0][0])} {fmt(points[0][1])}"
    ]

    for x, y in points[1:]:
        parts.append(
            f"L {fmt(x)} {fmt(y)}"
        )

    # Close the path if the original path was effectively closed.
    first = points[0]
    last = points[-1]

    if math.hypot(
        first[0] - last[0],
        first[1] - last[1]
    ) < 1e-6:
        parts.append("Z")

    d = " ".join(parts)

    svg = f'''<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg"
     viewBox="0 0 1000 1000">
  <path d="{d}"/>
</svg>
'''

    with open(filename, "w", encoding="utf-8") as f:
        f.write(svg)


def main():
    if len(sys.argv) not in (3, 4):
        print(
            "Usage:\n"
            "  python svg_converter.py input.svg output.svg [curve_steps]\n\n"
            "Example:\n"
            "  python svg_converter.py svgs/excali1.svg "
            "svgs/excali1_simple.svg 40"
        )
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    curve_steps = int(sys.argv[3]) if len(sys.argv) == 4 else 40

    print(f"Reading: {input_file}")

    d = find_path(input_file)

    print("Parsing path...")
    points = parse_path(d, curve_steps)

    print(f"Generated {len(points)} line-segment points.")

    print(f"Writing: {output_file}")
    write_svg(points, output_file)

    print("Done.")


if __name__ == "__main__":
    main()