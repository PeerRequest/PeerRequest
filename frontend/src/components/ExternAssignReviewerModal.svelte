<script>
    import {
        Button,
        ButtonGroup,
        Checkbox,
        Chevron,
        Dropdown,
        Heading,
        Modal,
        Search,
        Table,
        TableBody,
        TableBodyCell,
        TableBodyRow,
        TableHead,
        TableHeadCell
    } from "flowbite-svelte";
    import mock_data from "../mock_data.js";


    let bidding = mock_data.bidding[0];


    export let show = false;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    };

    let strategy = "bidding";

    export let papers = [];
    let paper_matches = {};

    let query = "";
    let users = mock_data.users;
    let reviewers = [];

    function apply_query(q) {
        query = q;
    }


    function papers_drop_down_color(matches, reviewer) {
        const papers = Object.entries(matches).filter(([key, value]) => value && key.startsWith(reviewer.id.toString())).length;
        return (papers > 0 ? "green" : "yellow");
    }

    function papers_drop_down_text(matches, reviewer) {
        const papers = Object.entries(matches).filter(([key, value]) => value && key.startsWith(reviewer.id.toString())).length;
        return (papers === 0 ? "No papers assigned" : papers === 1 ? "1 paper assigned" : papers + " papers assigned");
    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} title="Assign Reviewers">
    <div class="flex flex-row justify-between items-center">
        <div>
            <Heading size="md" tag="h4">Strategy</Heading>
        </div>
        <ButtonGroup>
            <Button color={strategy === "bidding" ? "primary" : "alternative"}
                    on:click={() => strategy = "bidding"}>
                Bidding
            </Button>
            <Button color={strategy === "direct" ? "primary" : "alternative"}
                    on:click={() => strategy = "direct"}>
                Direct
                Assignment
            </Button>
        </ButtonGroup>
    </div>

    <hr>

    <div class="flex flex-row justify-between items-center">
        <div>
            <Heading size="lg" tag="h4">Reviewers</Heading>
        </div>
        <div>
            <Button color="primary">
                <Chevron>Add Reviewer</Chevron>
            </Button>
            <Dropdown class="overflow-y-auto px-3 pb-3 text-sm h-44" on:show={() => apply_query("")}>
                <div class="p-3" slot="header">
                    <Search on:input={(e) => apply_query(e.target.value)} on:keyup={(e) => apply_query(e.target.value)}
                            size="md"/>
                </div>
                {#each users.filter(u => !reviewers.includes(u) && (query === "" || u.name.toLowerCase().includes(query.toLowerCase()))) as u }
                    <li class="rounded p-2 hover:bg-gray-100 dark:hover:bg-gray-600 font-semibold">
            <span class="cursor-pointer" on:click={() => reviewers = reviewers.concat([u]) }>
              {u.name}
            </span>
                    </li>
                {/each}
            </Dropdown>
        </div>
    </div>

    <div class="h-[50vh]">
        <Table divClass="relative">
            <TableHead>
                <TableHeadCell>Name</TableHeadCell>
                <TableHeadCell>Paper</TableHeadCell>
                <TableHeadCell></TableHeadCell>
            </TableHead>
            <TableBody class="divide-y">
                {#each reviewers as r }
                    <TableBodyRow>
                        <TableBodyCell>{r.name}</TableBodyCell>

                        <TableBodyCell>
                            <Button disabled={strategy === "bidding"} outline
                                    color={strategy === "direct" ? papers_drop_down_color(paper_matches, r) : "dark"}
                                    on:click={() => {
                        /*

                            Thanks to gods of glorious JavaScript, the checkboxes are always unchecked until
                            an update is triggered for paper_matches.

                            TODO: find a better way to ensure the right checkboxes are checked.

                        */
                        paper_matches = paper_matches;
                      }}
                            >
                                <Chevron>{strategy === "direct" ? papers_drop_down_text(paper_matches, r) : "Assignment through bidding"}</Chevron>
                            </Button>
                            <Dropdown class="w-44 overflow-y-auto p-3 space-y-3 text-sm">
                                {#each papers as p }
                                    <li>
                                        <Checkbox checked={paper_matches[r.id + ":" + p.id]}
                                                  on:change={(e) => paper_matches[r.id + ":" + p.id] = e.target.checked}>{p.title}</Checkbox>
                                    </li>
                                {/each}
                            </Dropdown>
                        </TableBodyCell>

                        <TableBodyCell>
                            <div class="flex flex-wrap items-center gap-2">
                                <Button pill class="!p-2" outline color="red"
                                        on:click={() => reviewers = reviewers.filter(e => e !== r)}>
                                    <svg class="w-5 h-5" xmlns="http://www.w3.org/2000/svg" x="0px" y="0px"
                                         width="32px" height="32px" viewBox="0 0 64 64"
                                         enable-background="new 0 0 64 64"
                                         xml:space="preserve">
                      <g>
                        <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="18.947"
                              y1="17.153" x2="45.045"
                              y2="43.056"/>
                      </g>
                                        <g>
                        <line fill="none" stroke="#000000" stroke-width="2" stroke-miterlimit="10" x1="19.045"
                              y1="43.153" x2="44.947"
                              y2="17.056"/>
                      </g>
                  </svg>
                                </Button>
                            </div>
                        </TableBodyCell>
                    </TableBodyRow>
                {/each}
            </TableBody>
        </Table>
    </div>

    <svelte:fragment slot="footer">
        <Button color="primary"
                href={bidding.category.id + (strategy === "direct" ? "/assignment/" : "/bidding/") + bidding.id}
                on:click={() => result(strategy === "direct", strategy === "direct" ?
            Object.fromEntries(Object.entries(paper_matches).filter(([key, value]) =>
            reviewers.some(r => value && key.startsWith(r.id.toString())))) : {})}>{strategy === "direct" ?
            "Assign" : "Start bidding"}</Button>
        <Button color="alternative" on:click={() => show=false}>Abort</Button>
    </svelte:fragment>
</Modal>