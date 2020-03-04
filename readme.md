Repo to report a bug on https://github.com/cljfx/cljfx

# Run the example
```
clj -m bug.main
```

# Steps to reproduce

- Slowly click and drag a rectangle.
- "Rendering diagram" should not print on the console since only node component subscription is changing.
- Keep dragging a rectangle enough and diagram-view component will start re rendering even when the only subscription it depends on is not changing.
  Rerender frequency increments with time you keep dragging any rectangle.
